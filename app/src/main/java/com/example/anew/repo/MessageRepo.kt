package com.example.anew.repo

import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.anew.model.Conversation
import com.example.anew.model.ConversationInfo
import com.example.anew.model.Message
import com.example.anew.model.User
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.jvm.java

class MessageRepo {
    private val db = FirebaseDatabase.getInstance()
    //save basic information
    val chatRef = db.getReference("chats")
    //save all message
    val messageRef = db.getReference("messages")
    //save all conversation forEach user
    val userChatRef = db.getReference("user_chats")

    // add message to chats and messages database
    suspend fun pushMessage(chatId: String,
                            chatName:String,

                            senderId: String,
                            nameSender: String,
                            avatar: String,

                            receiverId: String,
                            receiverName: String,
                            receiverAvatar: String,

                            lastMessage: String,
                            imageUrlList: List<String>,
                            chatType: String
                            ) {
            val key = messageRef.child(chatId).push().key
            if(key==null) return
            val message =
                Message(key, senderId,
                    lastMessage, imageUrlList, System.currentTimeMillis())

            val childUpdates = mutableMapOf<String, Any>()

            childUpdates["/messages/$chatId/$key"] = message

            if(chatType=="Private"){
                childUpdates["/user_chats/$senderId/$chatType/$chatId"] = Conversation(
                    chatId, receiverName, senderId, nameSender, receiverAvatar,
                    "You: $lastMessage", System.currentTimeMillis(), true
                )
                childUpdates["/user_chats/$receiverId/$chatType/$chatId"] = Conversation(
                    chatId, nameSender, senderId, nameSender, avatar,
                    "$nameSender: $lastMessage", System.currentTimeMillis(), false
                )
            }else{
                val info = chatRef.child(chatId).get().await()
                val getInfo = info.getValue(ConversationInfo::class.java)

                val conservation = Conversation(
                    chatId, chatName, senderId, nameSender, getInfo!!.avatar,
                    "$nameSender: $lastMessage", System.currentTimeMillis(), false
                )

                for(userId in getInfo!!.users){
                    if(userId==senderId) childUpdates["/user_chats/$userId/$chatType/$chatId"] = conservation.copy(isRead = true, lastMessage = "You: $lastMessage")
                    else childUpdates["/user_chats/$userId/$chatType/$chatId"] = conservation
                }
            }

            try {
                db.reference.updateChildren(childUpdates).await()
            }catch (e: Exception){
                Log.d("MessageRepo", "pushMessage: Error")
            }
    }

    suspend fun getMessages(groupId: String): Flow<List<Message>> = callbackFlow {
        val query = messageRef.child(groupId).limitToLast(50)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull { child ->
                    child.getValue(Message::class.java)
                }
                trySend(messages.sortedBy { it.time })
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        query.addValueEventListener(listener)
        awaitClose {
            query.removeEventListener(listener)
        }
    }


    suspend fun getConversation(userId:String, chatType: String): Flow<List<Conversation>> =
        callbackFlow {
            val valueListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val conversations = mutableListOf<Conversation>()
                    for (data in snapshot.children) {
                        val conversation = data.getValue(Conversation::class.java)
                        conversations.add(conversation!!)
                    }
                    trySend(if(conversations.isNullOrEmpty()) emptyList() else conversations.sortedByDescending { it.lastMessageTime })
                }
                override fun onCancelled(error: DatabaseError) {

                }
            }
            val query = userChatRef.child(userId).child(chatType)
            query.addValueEventListener(valueListener)
            awaitClose {
                query.removeEventListener(valueListener)
            }
        }

    suspend fun createGroup(name: String,avatar:String,adminId: String, users: List<String>,groupType: String){
        val key = chatRef.push().key
        if(key==null) return

        val info = ConversationInfo(key, name, avatar, adminId, users)
        val childUpdates = mutableMapOf<String, Any>()
        childUpdates["/chats/$key"] = info
        for(user in users){
            childUpdates["/user_chats/$user/$groupType/$key"] =
                Conversation(
                    key,
                    name,
                    adminId,
                    name,
                    avatar,
                    "Say hi to start conversation",
                    System.currentTimeMillis(),
                    true)
        }

        try {
            db.reference.updateChildren(childUpdates).await()
            Log.d("MessageRepo", "createGroup: Success")
        }catch (e: Exception){
            Log.d("MessageRepo", "createGroup: Error")
        }
    }

 ////  note
    suspend fun updateSeen(groupId: String, chatType: String, userId: String){
        userChatRef.child(userId).child(chatType)
            .child(groupId).updateChildren(mapOf("isRead" to true))
    }
}
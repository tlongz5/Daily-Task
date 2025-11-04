package com.example.anew.repo

import com.example.anew.model.PagingResult
import com.example.anew.model.User
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FriendRepo {
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun loadFriendsPage(
        userId: String,
        startAfterId: String?,
        limit: Int
    ): PagingResult {
        var query = db.collection("users").document(userId)
            .collection("friends")
            .orderBy(FieldPath.documentId())
            .limit(limit.toLong())

        if (startAfterId != null) {
            val startAfterDoc = db.collection("users").document(userId)
                .collection("friends").document(startAfterId).get().await()

            query = query.startAfter(startAfterDoc)
        }

        val snapshot = query.get().await()
        val friendUids = snapshot.documents.map { it.id }

        val friends = if(friendUids.isNotEmpty()){
            db.collection("users")
                .whereIn(FieldPath.documentId(), friendUids)
                .get().await()
                .toObjects(User::class.java)
        }else emptyList()

        val lastFriendId= if(friendUids.isNotEmpty() && friendUids.size == limit){
            friendUids.last()
        }else null

        return PagingResult(friends,lastFriendId)
    }


//note
    suspend fun getFriendList(uid: String): MutableList<String>{
        val snapshot = db.collection("users").document(uid)
            .collection("friends").document(uid).get().await()
        return snapshot.get("friendList") as MutableList<String>
    }

    suspend fun checkReceivedFriendInvite(uid: String, friendId: String): Boolean{
        val snapshot = db.collection("friends").document(uid).get().await()
        val receivedInvite = snapshot.get("receivedFriendInvite") as List<String>
        return receivedInvite.contains(friendId)
    }

    suspend fun checkSentFriendInvite(uid: String, friendId: String): Boolean{
        val snapshot = db.collection("friends").document(uid).get().await()
        val sentInvite = snapshot.get("sentFriendInvite") as List<String>
        return sentInvite.contains(friendId)
    }

}
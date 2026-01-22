package com.example.anew.repo

import android.util.Log
import com.example.anew.R
import com.example.anew.model.Notification
import com.example.anew.model.PagingResult
import com.example.anew.model.User
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

// receivedFriendInvite : from other user to accept
// friendsRequested : from me waiting for accept
// friends : friends
class FriendRepo(private val notificationRepo: NotificationRepo) {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

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

        val friends = if (friendUids.isNotEmpty()) {
            db.collection("users")
                .whereIn(FieldPath.documentId(), friendUids)
                .get().await()
                .toObjects(User::class.java)
        } else emptyList()

        val lastFriendId = if (friendUids.isNotEmpty() && friendUids.size == limit) {
            friendUids.last()
        } else null

        return PagingResult(friends, lastFriendId)
    }


    //note
    suspend fun getFriendList(uid: String): List<String> {
        val snapshot = db.collection("users").document(uid)
            .collection("friends").get().await()
        return snapshot.documents.map { it.id }
    }

    suspend fun getFriendListUser(uid: String): List<String> {
        val snapshot = db.collection("users").document(uid)
            .collection("friends").get().await()
        return snapshot.documents.map { it.id }
    }


    suspend fun checkFriend(uid: String, friendId: String): Int {
        val snapshot = db.collection("users").document(uid)
            .collection("friends").document(friendId).get().await()
        if (snapshot.exists()) return 1

        val snapshot2 = db.collection("users").document(uid)
            .collection("friendsRequested").document(friendId).get().await()
        if (snapshot2.exists()) return 0
        return -1
    }

    suspend fun requestFriend( currentUser: User, friendId: String) {
        val data = db.collection("users").document( currentUser.uid )
            .collection("receivedFriendInvite").document(friendId).get().await()

        val batch = db.batch()

        val solve1 = db.collection("users").document( currentUser.uid)
            .collection("friends").document(friendId)
        val solve2 = db.collection("users").document(friendId)
            .collection("friends").document( currentUser.uid)
        val solve3 = db.collection("users").document(friendId)
            .collection("receivedFriendInvite").document(friendId)
        val solve4 = db.collection("users").document(friendId)
            .collection("friendsRequested").document( currentUser.uid)

        val reverseSolve1 = db.collection("users").document( currentUser.uid)
            .collection("friendsRequested").document(friendId)
        val reverseSolve2 = db.collection("users").document(friendId)
            .collection("receivedFriendInvite").document( currentUser.uid)

        val notifiBecomeFriend = Notification(
            UUID.randomUUID().toString(),
            currentUser.name,
            "accepted your friend request",
            System.currentTimeMillis(),
            false,
            currentUser.photoUrl,
            "become_friend",
            "",
            friendId,
            currentUser.uid
        )

        val notifiRequest = Notification(
            UUID.randomUUID().toString(),
            "Friend Request",
            "You have a new friend request, check now",
            System.currentTimeMillis(),
            false,
            "",
            "request_friend",
            "",
            "",
            ""
        )

        if (data.exists()) {
            batch.set(solve1, mapOf<String, Any>())
            batch.set(solve2, mapOf<String, Any>())
            batch.delete(solve3)
            batch.delete(solve4)
            notificationRepo.addNotifyToTransaction(batch, notifiBecomeFriend)
        } else {
            batch.set(reverseSolve1, mapOf<String, Any>())
            batch.set(reverseSolve2, mapOf<String, Any>())
            notificationRepo.addNotifyToTransaction(batch, notifiRequest)
        }
        try {
            batch.commit().await()
        } catch (e: Exception) {
            Log.d("FriendRepo", "Error Request Friend")
        }
    }

    suspend fun addFriend(uid: String, friendId: User) {
        val batch = db.batch()
        val solve1 = db.collection("users").document(uid)
            .collection("friends").document(friendId.uid)
        val solve2 = db.collection("users").document(friendId.uid)
            .collection("friends").document(uid)

        val delete1 = db.collection("users").document(friendId.uid)
            .collection("friendsRequested").document(uid)
        val delete2 = db.collection("users").document(uid)
            .collection("receivedFriendInvite").document(friendId.uid)
        val notifiBecomeFriend = Notification(
            UUID.randomUUID().toString(),
            friendId.name,
            "accepted your friend request",
            System.currentTimeMillis(),
            false,
            friendId.photoUrl,
            "become_friend",
            "",
            friendId.uid,
            uid
        )

        try {
            batch.set(solve1, mapOf<String, Any>())
            batch.set(solve2, mapOf<String, Any>())
            batch.delete(delete1)
            batch.delete(delete2)
            notificationRepo.addNotifyToTransaction(batch, notifiBecomeFriend)
            batch.commit().await()
        } catch (e: Exception) {
            Log.d("FriendRepo", "Error Add Friend")
        }
    }

    suspend fun removeRequestFriend(uid: String, friendId: String) {
        val batch = db.batch()
        val solve1 = db.collection("users").document(friendId)
            .collection("friendsRequested").document(uid)
        val solve2 = db.collection("users").document(uid)
            .collection("receivedFriendInvite").document(friendId)
        try {
            batch.delete(solve1)
            batch.delete(solve2)
            batch.commit().await()
        } catch (e: Exception) {
            Log.d("FriendRepo", "Error Remove Request Friend")
        }
    }

    suspend fun unFriend(uid: String, friendId: String) {
        val batch = db.batch()
        val solve1 = db.collection("users").document(uid)
            .collection("friends").document(friendId)
        val solve2 = db.collection("users").document(friendId)
            .collection("friends").document(uid)
        try {
            batch.delete(solve1)
            batch.delete(solve2)
            batch.commit().await()
        } catch (e: Exception) {
            Log.d("FriendRepo", "Error UnFriend")
        }
    }

    suspend fun fromRequestToCancel(uid: String, friendId: String) {
        val batch = db.batch()
        val solve1 = db.collection("users").document(uid)
            .collection("friendsRequested").document(friendId)

        val solve2 = db.collection("users").document(friendId)
            .collection("receivedFriendInvite").document(uid)
        try {
            batch.delete(solve1)
            batch.delete(solve2)
            batch.commit().await()
        } catch (e: Exception) {
            Log.d("FriendRepo", "Error Cancel Request")
        }
    }

    suspend fun getFriendsRequest(uid: String): List<String> {
        try {
            val snapshot = db.collection("users").document(uid)
                .collection("receivedFriendInvite").get().await()
            return snapshot.documents.map { it.id }
        } catch (e: Exception) {
            Log.d("FriendRepo", "Error getFriendsRequest")
        }
        return emptyList()
    }


}
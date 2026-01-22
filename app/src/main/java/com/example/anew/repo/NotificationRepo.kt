package com.example.anew.repo

import android.util.Log
import androidx.room.Query
import com.example.anew.model.Notification
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import com.google.firebase.firestore.WriteBatch
import kotlinx.coroutines.tasks.await

class NotificationRepo {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getNotification(uid: String): List<Notification> {
        return try {
            db.collection("notifications")
                .whereEqualTo("userId", uid)
                .get()
                .await()
                .toObjects(Notification::class.java)
        } catch (e: Exception) {
            Log.d("Notification", e.message.toString())
            emptyList()
        }

    }

    fun addNotifyToTransaction(batch: WriteBatch, notification: Notification){
        batch.set(db.collection("notifications")
            .document(notification.notificationId),
            notification)
    }

    fun updateStatus(notificationId: String){
        db.collection("notifications")
            .document(notificationId)
            .update("checkRead", true)
            .addOnSuccessListener {
                Log.d("Notification", "Update status successfully")
            }
            .addOnFailureListener {
                Log.d("Notification", it.message.toString())
            }
    }
}
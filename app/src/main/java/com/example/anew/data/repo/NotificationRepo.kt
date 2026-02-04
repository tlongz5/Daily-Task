package com.example.anew.data.repo

import android.util.Log
import com.example.anew.data.local.MyHelper
import com.example.anew.model.Notification
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
        updateCounter(batch, true, notification.userId)
    }

    suspend fun updateStatus(notificationId: String){
        try {
            db.runBatch { batch ->
                val docRef = db.collection("notifications")
                    .document(notificationId)
                batch.update(docRef, "checkRead", true)
                updateCounter(batch, false, MyHelper.user!!.uid)
            }.await()
        }catch (e: Exception){
            Log.d("Notification", e.message.toString())
        }
    }

    suspend fun initCounter(uid: String) {
        val data = db.collection("counter")
            .document(uid).get().await()
        if (!data.exists()) {
            db.collection("counter")
                .document(uid)
                .set(hashMapOf("count" to 0))
                .await()
        }
    }

    fun updateCounter(batch: WriteBatch,isIncrease: Boolean,uid: String){
        val docRef = db.collection("counter")
            .document(uid)
        val value = if (isIncrease) 1L else -1L
        batch.update(docRef, "count", FieldValue.increment(value))
    }

    fun getCounter(uid: String): Flow<Long> = callbackFlow {
        val docRef = db.collection("counter")
            .document(uid)

        val listener = docRef.addSnapshotListener { snapshot, _ ->
            if(snapshot != null && snapshot.exists()){
                trySend(snapshot.getLong("count")!!)
            }
        }

        awaitClose { listener.remove() }
    }
}
package com.example.anew.repo

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.anew.R
import com.example.anew.model.User
import com.example.anew.model.fakeData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.io.File

class AuthRepo {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()


    fun getSignInIntent(context: Context) : Intent {
        val gso= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso).signInIntent
    }

    fun getCurrentUser() : FirebaseUser? = auth.currentUser

    suspend fun signInWithGoogle(idToken: String) : FirebaseUser? {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            return result.user
        }catch (e: Exception){
            null
        }
    }

    suspend fun signOut(context: Context){
        auth.signOut()
        val gso= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(context.getString(R.string.client_id))
            .build()
        GoogleSignIn.getClient(context, gso).signOut().await()

        //clear from sharePreference
        val sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
//        fakeData.uid = null
//        fakeData.name = null
//        fakeData.email = null
//        fakeData.avatarUrl = null
//        fakeData.phoneNumber = null
    }

    suspend fun getDataUser(user: User): User{
        val data = db.collection("users")
            .document(user.uid)
            .get()
            .await()
        return if(data.exists()) data.toObject(User::class.java)!! else user
    }

    suspend fun initUser(context: Context, user: User){
        MediaManager.get().upload(user.photoUrl)
            .unsigned(getString(context,R.string.upload_preset))
            .callback(object : UploadCallback{
                override fun onStart(requestId: String?) {

                }

                override fun onProgress(
                    requestId: String?,
                    bytes: Long,
                    totalBytes: Long
                ) {

                }

                override fun onSuccess(
                    requestId: String?,
                    resultData: Map<*, *>?
                ) {
                    val url = resultData?.get("url").toString()
                    user.photoUrl = url

                    val data = db.collection("users")
                        .document(user.uid)
                        .set(user)
                    Log.d("User", "User ${user.uid} created successfully")
                }

                override fun onError(
                    requestId: String?,
                    error: ErrorInfo?
                ) {
                    Log.d("Cloudinary upload image", "Error")
                }

                override fun onReschedule(
                    requestId: String?,
                    error: ErrorInfo?
                ) {

                }
            })
    }

    suspend fun getUserDataFromUid(uid: String): User{
        val data = db.collection("users")
            .document(uid)
            .get()
            .await()
        return data.toObject(User::class.java)!!
    }

    suspend fun checkEmailExist(email: String): Boolean {
        val data = db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .await()
        return !data.isEmpty
    }
    suspend fun getUidDataFromEmail(email: String): String{
        val data = db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .await()
        return data.documents[0].id
    }

}
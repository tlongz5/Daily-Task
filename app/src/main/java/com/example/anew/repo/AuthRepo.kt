package com.example.anew.repo

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.anew.R
import com.example.anew.model.User
import com.example.anew.support.convertUriToCloudinaryUrl
import com.example.anew.support.deleteUserFromSharePref
import com.example.anew.support.fakeData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepo {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()


    fun getSignInIntent(context: Context): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso).signInIntent
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    suspend fun signInWithGoogle(idToken: String): FirebaseUser? {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            return result.user
        } catch (e: Exception) {
            Log.d("Login", "signInWithGoogle failed")
            null
        }
    }

    suspend fun signOut(context: Context) {
        auth.signOut()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(context.getString(R.string.client_id))
            .build()
        GoogleSignIn.getClient(context, gso).signOut().await()

        deleteUserFromSharePref(context)
    }

    suspend fun getDataUser(user: User): User? {
        val data = db.collection("users")
            .document(user.uid)
            .get()
            .await()
        return if (data.exists()) data.toObject(User::class.java) else null
    }

    suspend fun updateProfile(name: String, username: String, phoneNumber: String) {
        val data = hashMapOf(
            "name" to name,
            "username" to username,
            "phoneNumber" to phoneNumber
        )
        db.collection("users")
            .document(fakeData.user!!.uid)
            .update(data as Map<String, Any>)
            .addOnSuccessListener {
                Log.d("User", "User updated successfully")
            }
            .addOnFailureListener {
                Log.d("User", "User updated failed")
                throw Exception("User updated failed")
            }
    }

    suspend fun updateAvatar(imageUri: String) {
        val data = hashMapOf(
            "photoUrl" to imageUri
        )
        db.collection("users")
            .document(fakeData.user!!.uid)
            .update(data as Map<String, Any>)
            .addOnSuccessListener {
                Log.d("User", "User updated successfully")
            }
            .addOnFailureListener {
                Log.d("User", "User updated failed")
                throw Exception("User updated failed")
            }
    }

    suspend fun initUser(user: User) {
        val url = convertUriToCloudinaryUrl(user.photoUrl)
        user.photoUrl = url

        db.collection("users")
            .document(user.uid)
            .set(user)
            .addOnSuccessListener {
                Log.d("User", "User created successfully")
            }
            .addOnFailureListener {
                Log.d("User", "User created failed")
            }
    }

    suspend fun getUserDataFromUid(uid: String): User {
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

    suspend fun getUidDataFromEmail(email: String): String {
        val data = db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .await()
        return data.documents[0].id
    }

    //if user already exists, user can't change it
    suspend fun checkDuplicateUsername(user: User): Boolean {
        val data = db.collection("users")
            .whereEqualTo("username", user.username)
            .get()
            .await()
        return data.documents[0].toObject(User::class.java)!!.uid != user.uid
    }

}
package com.example.anew

import android.app.Application
import com.cloudinary.android.MediaManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        val config= HashMap<String, String>()
        config["cloud_name"] = getString(R.string.cloud_name)
        MediaManager.init(this, config)

        FirebaseFirestore.getInstance().firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()

    }
}
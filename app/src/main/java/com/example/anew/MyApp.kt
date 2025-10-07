package com.example.anew

import android.app.Application
import com.cloudinary.android.MediaManager

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        val config= HashMap<String, String>()
        config["cloud_name"] = getString(R.string.cloud_name)
        MediaManager.init(this, config)
    }
}
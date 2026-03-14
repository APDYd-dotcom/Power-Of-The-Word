package com.poweroftheword.poweroftheword

import android.app.Application
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PowerOfTheWordApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Subscribe to church updates topic
        FirebaseMessaging.getInstance().subscribeToTopic("all")
    }
}

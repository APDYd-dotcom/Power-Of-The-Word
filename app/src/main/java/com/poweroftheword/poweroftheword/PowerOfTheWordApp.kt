package com.poweroftheword.poweroftheword

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.poweroftheword.poweroftheword.service.FCMTopicManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class PowerOfTheWordApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var fcmTopicManager: FCMTopicManager

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        // Subscribe to "all_users" topic for global notifications
        fcmTopicManager.subscribeToAllUsers()
    }
}

package com.poweroftheword.poweroftheword

import android.app.Application
import androidx.work.Configuration
import com.google.firebase.FirebaseApp
import com.poweroftheword.poweroftheword.di.appModule
import com.poweroftheword.poweroftheword.service.FCMTopicManager
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.factory.KoinWorkerFactory
import org.koin.core.context.startKoin

class PowerOfTheWordApp : Application(), Configuration.Provider {

    private val fcmTopicManager: FCMTopicManager by inject()

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(KoinWorkerFactory())
            .build()

    override fun onCreate() {
        super.onCreate()
        
        // Explicit Firebase initialization
        FirebaseApp.initializeApp(this)
        
        startKoin {
            androidLogger()
            androidContext(this@PowerOfTheWordApp)
            modules(appModule)
        }

        // Subscribe to "all_users" topic for global notifications
        fcmTopicManager.subscribeToAllUsers()
    }
}

package com.poweroftheword.poweroftheword.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FCMTopicManager @Inject constructor() {

    private val allLanguageTopics = listOf("lang_en", "lang_fr", "lang_sw", "lang_rw")

    /**
     * Updates the language topic by unsubscribing from all other language topics
     * and subscribing to the new one.
     * @param languageCode The language code from DataStore (e.g., "en", "fr", "sw", "rw")
     */
    fun updateLanguageTopic(languageCode: String) {
        val targetTopic = mapLanguageToTopic(languageCode)
        
        Log.d(TAG, "Updating language topic to: $targetTopic (from code: $languageCode)")

        // 1. Unsubscribe from all other language topics to avoid duplicates
        allLanguageTopics.filter { it != targetTopic }.forEach { topic ->
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Unsubscribed from topic: $topic")
                    }
                }
        }

        // 2. Subscribe to the target topic
        FirebaseMessaging.getInstance().subscribeToTopic(targetTopic)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Successfully subscribed to topic: $targetTopic")
                } else {
                    Log.e(TAG, "Failed to subscribe to topic: $targetTopic", task.exception)
                }
            }
    }

    /**
     * Subscribes the device to the general "all_users" topic for global announcements.
     */
    fun subscribeToAllUsers() {
        FirebaseMessaging.getInstance().subscribeToTopic("all_users")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Successfully subscribed to topic: all_users")
                } else {
                    Log.e(TAG, "Failed to subscribe to topic: all_users", task.exception)
                }
            }
    }

    private fun mapLanguageToTopic(code: String): String {
        return when (code.lowercase()) {
            "en" -> "lang_en"
            "fr" -> "lang_fr"
            "sw" -> "lang_sw"
            "rw", "ki", "rn" -> "lang_ki"
            else -> "lang_en" // Default to English
        }
    }

    companion object {
        private const val TAG = "FCMTopicManager"
    }
}

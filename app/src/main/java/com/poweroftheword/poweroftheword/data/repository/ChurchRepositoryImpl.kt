package com.poweroftheword.poweroftheword.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.poweroftheword.poweroftheword.domain.model.DailyWord
import com.poweroftheword.poweroftheword.domain.model.Video
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "settings")

class ChurchRepositoryImpl @Inject constructor(
    private val client: HttpClient,
    private val context: Context
) : ChurchRepository {

    private val LANGUAGE_KEY = stringPreferencesKey("language_preference")

    override suspend fun getVideos(language: String): List<Video> {
        // Implementation for Ktor fetch will go here
        return emptyList()
    }

    override suspend fun getDailyWord(language: String): DailyWord? {
        // Implementation for Ktor fetch will go here
        return null
    }

    override fun getSavedLanguage(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[LANGUAGE_KEY] ?: "English"
        }
    }

    override suspend fun saveLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }
}

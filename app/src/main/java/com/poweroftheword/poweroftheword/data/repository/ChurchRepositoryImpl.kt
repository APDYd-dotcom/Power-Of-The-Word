package com.poweroftheword.poweroftheword.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.poweroftheword.poweroftheword.domain.model.*
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "settings")

class ChurchRepositoryImpl @Inject constructor(
    private val client: HttpClient,
    private val context: Context
) : ChurchRepository {

    private val BASE_URL = "https://power.clubtechlac.bi/api"
    private val LANGUAGE_KEY = stringPreferencesKey("language_preference")

    override suspend fun getVideos(language: String, type: String?): List<Video> {
        return client.get("$BASE_URL/videos/") {
            parameter("language", language)
            type?.let { parameter("type", it) }
        }.body()
    }

    override suspend fun getLiveStreams(): List<Live> {
        return client.get("$BASE_URL/live/").body()
    }

    override suspend fun getAudioSermons(language: String): List<Audio> {
        return client.get("$BASE_URL/audio/") {
            parameter("language", language)
        }.body()
    }

    override suspend fun getFeeds(language: String, type: String?): List<Feed> {
        return client.get("$BASE_URL/feeds/") {
            parameter("language", language)
            type?.let { parameter("type", it) }
        }.body()
    }

    override suspend fun getDailyWord(language: String): DailyWord? {
        return try {
            client.get("$BASE_URL/dailyword/") {
                parameter("language", language)
            }.body()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getRadioStatus(): Radio? {
        return try {
            client.get("$BASE_URL/radio/").body()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getPrograms(): List<Program> {
        return client.get("$BASE_URL/programs/").body()
    }

    override suspend fun getHoraire(language: String): List<Horaire> {
        return client.get("$BASE_URL/horaire/") {
            parameter("language", language)
        }.body()
    }

    override suspend fun recordVideoView(videoId: String, deviceId: String) {
        client.post("$BASE_URL/viewvideo/") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("video" to videoId, "device_id" to deviceId))
        }
    }

    override suspend fun likeVideo(videoId: String, deviceId: String) {
        client.post("$BASE_URL/likevideo/") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("video" to videoId, "device_id" to deviceId))
        }
    }

    override suspend fun shareVideo(videoId: String, deviceId: String) {
        client.post("$BASE_URL/sharevideo/") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("video" to videoId, "video_id" to videoId, "device_id" to deviceId))
        }
    }

    override suspend fun recordAudioListen(audioId: String, deviceId: String) {
        client.post("$BASE_URL/listenaudio/") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("audio" to audioId, "device_id" to deviceId))
        }
    }

    override suspend fun likeAudio(audioId: String, deviceId: String) {
        client.post("$BASE_URL/likeaudio/") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("audio" to audioId, "device_id" to deviceId))
        }
    }

    override suspend fun shareAudio(audioId: String, deviceId: String) {
        client.post("$BASE_URL/shareaudio/") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("audio" to audioId, "device_id" to deviceId))
        }
    }

    override suspend fun registerLiveViewer(liveId: String, deviceId: String) {
        client.post("$BASE_URL/viewlive/") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("live" to liveId, "device_id" to deviceId))
        }
    }

    override fun getSavedLanguage(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[LANGUAGE_KEY] ?: "EN"
        }
    }

    override suspend fun saveLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }
}

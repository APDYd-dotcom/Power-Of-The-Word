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

    private val BASE_URL = "https://poweroftheword.bi/api"
    private val LANGUAGE_KEY = stringPreferencesKey("language_preference")

    // Fixed static data with working YouTube IDs to resolve "Video Unavailable" error
    private val staticVideos = listOf(
        Video(
            id = "1",
            title = "Power of the Word - Weekly Message",
            description = "A powerful message to start your week with faith.",
            thumbnailUrl = "https://img.youtube.com/vi/q_V6o_KjS3A/maxresdefault.jpg",
            videoUrl = "https://www.youtube.com/watch?v=q_V6o_KjS3A",
            language = "EN",
            views = 15250,
            likes = 450,
            datePublished = "2023-10-01"
        ),
        Video(
            id = "2",
            title = "Morning Prayer and Devotion",
            description = "Join us for a peaceful morning of prayer.",
            thumbnailUrl = "https://img.youtube.com/vi/_OBlgSz8sSM/maxresdefault.jpg",
            videoUrl = "https://www.youtube.com/watch?v=_OBlgSz8sSM",
            language = "EN",
            views = 22100,
            likes = 320,
            datePublished = "2023-10-05"
        ),
        Video(
            id = "3",
            title = "Walking in Victory",
            description = "How to overcome challenges through the word.",
            thumbnailUrl = "https://img.youtube.com/vi/H6U6-368-uE/maxresdefault.jpg",
            videoUrl = "https://www.youtube.com/watch?v=H6U6-368-uE",
            language = "EN",
            views = 8900,
            likes = 850,
            datePublished = "2023-10-10"
        )
    )

    // Static test data for audios
    private val staticAudios = listOf(
        Audio(
            id = "1",
            title = "GUSENGA BIHINDURA IBINTU",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            date = "30-05-2025",
            language = "EN",
            listens = 1250,
            likes = 3100
        ),
        Audio(
            id = "2",
            title = "Power of the Word - Morning Devotion",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            date = "31 January 2026",
            language = "EN",
            listens = 890,
            likes = 450
        ),
        Audio(
            id = "3",
            title = "Walking in the Spirit",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            date = "29 January 2026",
            language = "EN",
            listens = 2100,
            likes = 850
        )
    )

    override suspend fun getVideos(language: String, type: String?): List<Video> {
        return try {
            val response: List<Video> = client.get("$BASE_URL/videos/") {
                parameter("language", language)
                type?.let { parameter("type", it) }
            }.body()
            
            if (response.isEmpty()) staticVideos else response
        } catch (e: Exception) {
            staticVideos
        }
    }

    override suspend fun getLiveStreams(): List<Live> {
        return try {
            client.get("$BASE_URL/live/").body()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getAudioSermons(language: String): List<Audio> {
        return try {
            val response: List<Audio> = client.get("$BASE_URL/audio/") {
                parameter("language", language)
            }.body()
            if (response.isEmpty()) staticAudios else response
        } catch (e: Exception) {
            staticAudios
        }
    }

    override suspend fun getFeeds(language: String, type: String?): List<Feed> {
        return try {
            client.get("$BASE_URL/feeds/") {
                parameter("language", language)
                type?.let { parameter("type", it) }
            }.body()
        } catch (e: Exception) {
            emptyList()
        }
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
        return try {
            client.get("$BASE_URL/programs/").body()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getHoraire(language: String): List<Horaire> {
        return try {
            client.get("$BASE_URL/horaire/") {
                parameter("language", language)
            }.body()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun recordVideoView(videoId: String, deviceId: String) {
        try {
            client.post("$BASE_URL/viewvideo/") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("video" to videoId, "device_id" to deviceId))
            }
        } catch (e: Exception) {}
    }

    override suspend fun likeVideo(videoId: String, deviceId: String) {
        try {
            client.post("$BASE_URL/likevideo/") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("video" to videoId, "device_id" to deviceId))
            }
        } catch (e: Exception) {}
    }

    override suspend fun shareVideo(videoId: String, deviceId: String) {
        try {
            client.post("$BASE_URL/sharevideo/") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("video" to videoId, "video_id" to videoId, "device_id" to deviceId))
            }
        } catch (e: Exception) {}
    }

    override suspend fun recordAudioListen(audioId: String, deviceId: String) {
        try {
            client.post("$BASE_URL/listenaudio/") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("audio" to audioId, "device_id" to deviceId))
            }
        } catch (e: Exception) {}
    }

    override suspend fun likeAudio(audioId: String, deviceId: String) {
        try {
            client.post("$BASE_URL/likeaudio/") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("audio" to audioId, "device_id" to deviceId))
            }
        } catch (e: Exception) {}
    }

    override suspend fun shareAudio(audioId: String, deviceId: String) {
        try {
            client.post("$BASE_URL/shareaudio/") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("audio" to audioId, "device_id" to deviceId))
            }
        } catch (e: Exception) {}
    }

    override suspend fun registerLiveViewer(liveId: String, deviceId: String) {
        try {
            client.post("$BASE_URL/viewlive/") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("live" to liveId, "device_id" to deviceId))
            }
        } catch (e: Exception) {}
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

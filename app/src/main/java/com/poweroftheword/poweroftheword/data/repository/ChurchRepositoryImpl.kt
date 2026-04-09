package com.poweroftheword.poweroftheword.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.poweroftheword.poweroftheword.BuildConfig
import com.poweroftheword.poweroftheword.domain.model.*
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import io.ktor.http.contentType
import io.ktor.http.formUrlEncode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.collections.emptyList

private val Context.dataStore by preferencesDataStore(name = "settings")

class ChurchRepositoryImpl @Inject constructor(
    private val client: HttpClient,
    private val context: Context
) : ChurchRepository {

    private val BASE_URL = BuildConfig.BASE_URLAPI
    private val LANGUAGE_KEY = stringPreferencesKey("language_preference")


    override suspend fun getVideos(language: String): List<VideoItem> {
        return try {
            Log.e("getVideos", "Language: $language")
            val response: String = client.get("$BASE_URL/getvideo/") {
                setBody(
                    TextContent(
                        "language=$language",
                        ContentType.Application.FormUrlEncoded
                    )
                )
            }.bodyAsText()

            val res = Json {
                ignoreUnknownKeys = true
            }.decodeFromString<Video>(response)
            Log.e("getVideos", "Response: $res")
            res.videos

        } catch (e: Exception) {
            Log.e("getVideos", "Error: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getLiveStreams(): List<Live> {
        return try {
            client.get("$BASE_URL/live/").body()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getAudio(language: String): List<AudioItem> {
        return try {
            Log.e("getAudio", "Language: $language")
            val response: String = client.get("$BASE_URL/getaudio/") {
                setBody(
                    TextContent(
                        "language=$language",
                        ContentType.Application.FormUrlEncoded
                    )
                )
            }.bodyAsText()

            val res = Json {
                ignoreUnknownKeys = true
            }.decodeFromString<Audio>(response)
            Log.e("getAudio", "Response: $res")
            res.audios

        } catch (e: Exception) {
            Log.e("getAudio", "Error: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getFeeds(language: String): List<FeedItem> {
        return try {

            val rawResponse: String = client.get("$BASE_URL/getfeed/") {
                setBody(
                    TextContent(
                        "language=$language",
                        ContentType.Application.FormUrlEncoded
                    )
                )
            }.bodyAsText()

            Log.e("getFeeds", "RAW: $rawResponse")

            val response = Json {
                ignoreUnknownKeys = true
            }.decodeFromString<Feed>(rawResponse)

            response.feeds

        } catch (e: Exception) {
            Log.e("getFeeds", "Error: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getDailyWord(language: String): List<DailyWordItem> {
        Log.e("getDailyWord", "Language: $language")
        return try {
            val response: String = client.get("$BASE_URL/getdailyword/") {
                setBody(
                    TextContent(
                        "language=$language",
                        ContentType.Application.FormUrlEncoded
                    )
                )
            }.bodyAsText()

            Log.e("getDailyWord", "Response: $response")

            val res = Json {
                ignoreUnknownKeys = true
            }.decodeFromString<DailyWord>(response)
            Log.e("getDailyWord", "Response: $res")
            res.dailywords

        } catch (e: Exception) {
            Log.e("getDailyWord", "Error: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getRadioStatus(): List<Radio> {
        return try {
            val response: String = client.get("$BASE_URL/radio/").bodyAsText()
            val res = Json {
                ignoreUnknownKeys = true
            }.decodeFromString<RadioResponse>(response)
            res.results
        } catch (e: Exception) {
            Log.e("getRadioStatus", "Error: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getPrograms(language: String): List<Program> {
        return try {
            Log.e("getPrograms", "Language: $language")
            val response: String = client.get("$BASE_URL/getprogram/"){
                setBody(
                    TextContent(
                        "language=$language",
                        ContentType.Application.FormUrlEncoded
                    )
                )
            }.bodyAsText()

            val res = Json {
                ignoreUnknownKeys = true
            }.decodeFromString<ProgramResponse>(response)
            Log.e("getPrograms", "Response: $res")
            res.programs

        } catch (e: Exception) {
            Log.e("getPrograms", "Error: ${e.message}", e)
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

    override suspend fun recordAudioListen(audioId: Int, deviceId: String) {
        try {
            client.post("$BASE_URL/listenaudio/") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("audio" to audioId, "device_id" to deviceId))
            }
        } catch (e: Exception) {}
    }

    override suspend fun likeAudio(audioId: Int, deviceId: String) {
        try {
            client.post("$BASE_URL/likeaudio/") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("audio" to audioId, "device_id" to deviceId))
            }
        } catch (e: Exception) {}
    }

    override suspend fun shareAudio(audioId: Int, deviceId: String) {
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

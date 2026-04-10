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
            Log.d("ChurchRepo", "getVideos | Language: $language")
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
            Log.d("ChurchRepo", "getVideos | Found ${res.videos.size} videos")
            res.videos

        } catch (e: Exception) {
            Log.e("ChurchRepo", "getVideos | Error: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getLiveStreams(): List<Live> {
        return try {
            Log.d("ChurchRepo", "getLiveStreams | Fetching...")
            val result: List<Live> = client.get("$BASE_URL/live/").body()
            Log.d("ChurchRepo", "getLiveStreams | Found ${result.size} streams")
            result
        } catch (e: Exception) {
            Log.e("ChurchRepo", "getLiveStreams | Error: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getAudio(language: String): List<AudioItem> {
        return try {
            Log.d("ChurchRepo", "getAudio | Language: $language")
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
            Log.d("ChurchRepo", "getAudio | Found ${res.audios.size} audios")
            res.audios

        } catch (e: Exception) {
            Log.e("ChurchRepo", "getAudio | Error: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getFeeds(language: String): List<FeedItem> {
        return try {
            Log.d("ChurchRepo", "getFeeds | Language: $language")
            val rawResponse: String = client.get("$BASE_URL/getfeed/") {
                setBody(
                    TextContent(
                        "language=$language",
                        ContentType.Application.FormUrlEncoded
                    )
                )
            }.bodyAsText()

            val response = Json {
                ignoreUnknownKeys = true
            }.decodeFromString<Feed>(rawResponse)
            Log.d("ChurchRepo", "getFeeds | Found ${response.feeds.size} items")
            response.feeds

        } catch (e: Exception) {
            Log.e("ChurchRepo", "getFeeds | Error: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getDailyWord(language: String): List<DailyWordItem> {
        return try {
            Log.d("ChurchRepo", "getDailyWord | Language: $language")
            val response: String = client.get("$BASE_URL/getdailyword/") {
                setBody(
                    TextContent(
                        "language=$language",
                        ContentType.Application.FormUrlEncoded
                    )
                )
            }.bodyAsText()

            val res = Json {
                ignoreUnknownKeys = true
            }.decodeFromString<DailyWord>(response)
            Log.d("ChurchRepo", "getDailyWord | Found ${res.dailywords.size} items")
            res.dailywords

        } catch (e: Exception) {
            Log.e("ChurchRepo", "getDailyWord | Error: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getRadioStatus(): List<Radio> {
        return try {
            Log.d("ChurchRepo", "getRadioStatus | Fetching...")
            val response: String = client.get("$BASE_URL/radio/").bodyAsText()
            val res = Json {
                ignoreUnknownKeys = true
            }.decodeFromString<RadioResponse>(response)
            Log.d("ChurchRepo", "getRadioStatus | Status: ${res.results.size}")
            res.results
        } catch (e: Exception) {
            Log.e("ChurchRepo", "getRadioStatus | Error: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getPrograms(language: String): List<Program> {
        return try {
            Log.d("ChurchRepo", "getPrograms | Language: $language")
            val response: String = client.get("$BASE_URL/getprogram/") {
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
            Log.d("ChurchRepo", "getPrograms | Found ${res.programs.size} programs")
            res.programs

        } catch (e: Exception) {
            Log.e("ChurchRepo", "getPrograms | Error: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getHoraire(language: String): List<Horaire> {
        return try {
            Log.d("ChurchRepo", "getHoraire | Language: $language")
            val result: List<Horaire> = client.get("$BASE_URL/horaire/") {
                parameter("language", language)
            }.body()
            Log.d("ChurchRepo", "getHoraire | Found ${result.size} entries")
            result
        } catch (e: Exception) {
            Log.e("ChurchRepo", "getHoraire | Error: ${e.message}")
            emptyList()
        }
    }

    override suspend fun recordVideoView(videoId: String, deviceId: String) {
        try {
            Log.d("ChurchRepo", "recordVideoView | videoId: $videoId, deviceId: $deviceId")
            val response = client.post("$BASE_URL/addviewvideo/") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("video" to videoId, "device_id" to deviceId))
            }
            Log.d("ChurchRepo", "recordVideoView | Success: ${response.status}")
        } catch (e: Exception) {
            Log.e("ChurchRepo", "recordVideoView | Error: ${e.message}")
        }
    }

    override suspend fun likeVideo(videoId: String, deviceId: String) {
        try {
            Log.d("ChurchRepo", "likeVideo | videoId: $videoId, deviceId: $deviceId")
            val response = client.post("$BASE_URL/addlikevideo/") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("video_id" to videoId, "device_id" to deviceId))
            }
            Log.d("ChurchRepo", "likeVideo | Success: ${response.status}")
        } catch (e: Exception) {
            Log.e("ChurchRepo", "likeVideo | Error: ${e.message}", e)
        }
    }

    override suspend fun getlikeVideo(videoId: String, deviceId: String): Fanta {
        return try {
            Log.d("ChurchRepo", "getlikeVideo | videoId: $videoId, deviceId: $deviceId")
            val response = client.post("$BASE_URL/checklike/") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("video_id" to videoId, "device_id" to deviceId))
            }.body<Fanta>()
            Log.d("ChurchRepo", "getlikeVideo | Success: ${response.fanta}")
            response
        } catch (e: Exception) {
            Log.e("ChurchRepo", "getlikeVideo | Error: ${e.message}", e)
            Fanta(fanta = false)
        }
    }

    override suspend fun shareVideo(videoId: String, deviceId: String) {
        try {
            Log.d("ChurchRepo", "shareVideo | videoId: $videoId, deviceId: $deviceId")
            val response = client.post("$BASE_URL/addsharevideo/") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("video" to videoId, "video_id" to videoId, "device_id" to deviceId))
            }
            Log.d("ChurchRepo", "shareVideo | Success: ${response.status}")
        } catch (e: Exception) {
            Log.e("ChurchRepo", "shareVideo | Error: ${e.message}")
        }
    }

    override suspend fun recordAudioListen(audioId: Int, deviceId: String) {
        try {
            Log.d("ChurchRepo", "recordAudioListen | audioId: $audioId")
            client.post("$BASE_URL/listenaudio/") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("audio" to audioId, "device_id" to deviceId))
            }
        } catch (e: Exception) {
            Log.e("ChurchRepo", "recordAudioListen | Error: ${e.message}")
        }
    }

    override suspend fun likeAudio(audioId: Int, deviceId: String) {
        try {
            Log.d("ChurchRepo", "likeAudio | audioId: $audioId")
            client.post("$BASE_URL/likeaudio/") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("audio" to audioId, "device_id" to deviceId))
            }
        } catch (e: Exception) {
            Log.e("ChurchRepo", "likeAudio | Error: ${e.message}")
        }
    }

    override suspend fun shareAudio(audioId: Int, deviceId: String) {
        try {
            Log.d("ChurchRepo", "shareAudio | audioId: $audioId")
            client.post("$BASE_URL/shareaudio/") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("audio" to audioId, "device_id" to deviceId))
            }
        } catch (e: Exception) {
            Log.e("ChurchRepo", "shareAudio | Error: ${e.message}")
        }
    }

    override suspend fun registerLiveViewer(liveId: String, deviceId: String) {
        try {
            Log.d("ChurchRepo", "registerLiveViewer | liveId: $liveId")
            client.post("$BASE_URL/viewlive/") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("live" to liveId, "device_id" to deviceId))
            }
        } catch (e: Exception) {
            Log.e("ChurchRepo", "registerLiveViewer | Error: ${e.message}")
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

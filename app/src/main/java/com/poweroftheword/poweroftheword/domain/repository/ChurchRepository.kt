package com.poweroftheword.poweroftheword.domain.repository

import com.poweroftheword.poweroftheword.domain.model.*
import kotlinx.coroutines.flow.Flow

interface ChurchRepository {
    // Media Fetching
    suspend fun getVideos(language: String): List<VideoItem>
    suspend fun getLiveStreams(): List<Live>
    suspend fun getAudio(language: String): List<AudioItem>
    suspend fun getFeeds(language: String): List<FeedItem>
    suspend fun getDailyWord(language: String): List<DailyWordItem>
    suspend fun getRadioStatus(): List<Radio>
    suspend fun getPrograms(language: String): List<Program>
    suspend fun getHoraire(language: String): List<Horaire>
    
    // Interactions
    suspend fun recordVideoView(videoId: String, deviceId: String)
    suspend fun likeVideo(videoId: String, deviceId: String)

    suspend fun getlikeVideo(videoId: String, deviceId: String): Fanta
    suspend fun shareVideo(videoId: String, deviceId: String)
    
    suspend fun recordAudioListen(audioId: Int, deviceId: String)
    suspend fun likeAudio(audioId: Int, deviceId: String)
    suspend fun shareAudio(audioId: Int, deviceId: String)

    suspend fun registerLiveViewer(liveId: String, deviceId: String)
    // Settings
    fun getSavedLanguage(): Flow<String>
    suspend fun saveLanguage(language: String)
}

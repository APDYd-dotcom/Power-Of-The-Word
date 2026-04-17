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
    suspend fun getHoraire(language: String): List<HoraireItem>
    
    // Interactions
    suspend fun recordVideoView(videoId: String, deviceId: String)
    suspend fun likeVideo(videoId: String, deviceId: String)

    suspend fun interactions(
        deviceId: String,
        videoId: String? = null,
        audioId: String? = null,
        feedId: String? = null,
        action: String
        ) : InteractionResponse

    suspend fun getlikeVideo(videoId: String, deviceId: String): Fanta

    suspend fun chackLike(
        deviceId: String,
        videoId: String? = null,
        audioId: String? = null,
        feedId: String? = null
    ) : InteractionResponse
    suspend fun shareVideo(videoId: String, deviceId: String)

    suspend fun getPastor(): List<PastorItem>

    
    suspend fun recordAudioListen(audioId: Int, deviceId: String)
    suspend fun likeAudio(audioId: Int, deviceId: String)
    suspend fun shareAudio(audioId: Int, deviceId: String)

    suspend fun registerLiveViewer(liveId: String, deviceId: String)

    suspend fun shareFeed(feedId: Int, deviceId: String)

    // Settings
    fun getSavedLanguage(): Flow<String>
    suspend fun saveLanguage(language: String)
    fun getSavedTheme(): Flow<Boolean?>
    suspend fun saveTheme(isDark: Boolean?)

    // Offline-first Likes
    suspend fun toggleVideoLikeLocal(videoId: String, deviceId: String)
    suspend fun syncMissingLikes(videos: List<VideoItem>, deviceId: String)
    
    suspend fun toggleAudioLikeLocal(audioId: String, deviceId: String)
    suspend fun syncMissingAudioLikes(audios: List<AudioItem>, deviceId: String)

    suspend fun toggleFeedLikeLocal(feedId: String, deviceId: String)
    suspend fun syncMissingFeedLikes(feeds: List<FeedItem>, deviceId: String)

    // Video View Tracking (TikTok-style)
    suspend fun recordVideoViewLocal(videoId: String)
    fun getVideoViewedFlow(): Flow<Set<String>>
}

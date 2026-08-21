package com.poweroftheword.poweroftheword.ui.screens.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poweroftheword.poweroftheword.data.local.FeedLikeDao
import com.poweroftheword.poweroftheword.data.local.VideoLikeDao
import com.poweroftheword.poweroftheword.domain.model.*
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import com.poweroftheword.poweroftheword.util.DeviceUtils
import com.poweroftheword.poweroftheword.R
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class HomeState(
    val dailyWord: DailyWord? = null,
    val liveStreams: List<LiveItem> = emptyList(),
    val latestVideos: List<VideoItem> = emptyList(),
    val latestFeeds: List<FeedItem> = emptyList(),
    val radioStatus: List<Radio> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentLanguage: String = "EN"
)

class HomeViewModel(
    private val repository: ChurchRepository,
    private val videoLikeDao: VideoLikeDao,
    private val feedLikeDao: FeedLikeDao,
    private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    val likedVideoIds: StateFlow<Set<String>> = videoLikeDao.getAllLikesFlow()
        .map { likes -> likes.filter { it.isLiked }.map { it.videoId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val likedFeedIds: StateFlow<Set<String>> = feedLikeDao.getAllLikesFlow()
        .map { likes -> likes.filter { it.isLiked }.map { it.feedId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    init {
        repository.getSavedLanguage()
            .onEach { lang ->
                _state.update { it.copy(currentLanguage = lang) }
                loadHomeData()
            }
            .launchIn(viewModelScope)
            
        startActiveStatusTicker()
    }

    private fun startActiveStatusTicker() {
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(60000) // Update every minute
                val rawRadios = repository.getRadioStatus()
                val radioStatus = rawRadios
                    .groupBy { it.radio?.id }
                    .mapNotNull { (_, items) ->
                        val firstItem = items.firstOrNull() ?: return@mapNotNull null
                        val radio = firstItem.radio ?: return@mapNotNull null
                        radio.copy(
                            isActive = items.any { isRadioCurrentlyActive(it) }
                        )
                    }
                _state.update { currentState ->
                    currentState.copy(radioStatus = radioStatus)
                }
            }
        }
    }

    fun changeLanguage(langCode: String) {
        viewModelScope.launch {
            repository.saveLanguage(langCode)
        }
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val language = repository.getSavedLanguage().first()
                val dailyWordItems = repository.getDailyWord(language)
                val liveStreams = repository.getLiveStreams(language)
                val latestVideos = repository.getVideos(language)
                val latestFeeds = repository.getFeeds(language)
                val rawRadios = repository.getRadioStatus()
                val radioStatus = rawRadios
                    .groupBy { it.radio?.id }
                    .mapNotNull { (_, items) ->
                        val firstItem = items.firstOrNull() ?: return@mapNotNull null
                        val radio = firstItem.radio ?: return@mapNotNull null
                        radio.copy(
                            isActive = items.any { isRadioCurrentlyActive(it) }
                        )
                    }

                _state.update {
                    it.copy(
                        dailyWord = DailyWord(dailyWordItems),
                        liveStreams = liveStreams,
                        latestVideos = latestVideos,
                        latestFeeds = latestFeeds,
                        radioStatus = radioStatus,
                        isLoading = false
                    )
                }
                
                val deviceId = DeviceUtils.getDeviceId(context)
                repository.syncMissingLikes(latestVideos, deviceId)
                repository.syncMissingFeedLikes(latestFeeds, deviceId)
                
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Failed to load updates") }
            }
        }
    }

    fun likeVideo(videoId: String) {
        viewModelScope.launch {
            val deviceId = DeviceUtils.getDeviceId(context)
            try {
                repository.toggleVideoLikeLocal(videoId, deviceId)
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to like video.") }
            }
        }
    }

    fun toggleFeedLike(feedId: String) {
        viewModelScope.launch {
            val deviceId = DeviceUtils.getDeviceId(context)
            try {
                repository.toggleFeedLikeLocal(feedId, deviceId)
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to like feed.") }
            }
        }
    }

    fun onVideoShared(videoId: String) {
        viewModelScope.launch {
            val deviceId = DeviceUtils.getDeviceId(context)
            try {
                Log.d("HomeViewModel", "Recording video share: $videoId")
                repository.shareVideo(videoId, deviceId)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Failed to record share on server", e)
            }
        }
    }

    private fun isRadioCurrentlyActive(radioItem: RadioItem): Boolean {
        val startStr = radioItem.startHour?.takeIf { it.isNotBlank() } ?: return false
        val endStr = radioItem.endHour?.takeIf { it.isNotBlank() } ?: return false
        val day = radioItem.day?.takeIf { it.isNotBlank() } ?: return false

        return try {
            val calendar = Calendar.getInstance()
            val currentDay = SimpleDateFormat("EEE", Locale.US).format(calendar.time).lowercase()
            
            // 1. Strict Day Check
            if (!day.equals(currentDay, ignoreCase = true)) return false

            // 2. Strict Time Check
            val sdf = SimpleDateFormat("HH:mm:ss", Locale.US)
            val now = sdf.parse(sdf.format(calendar.time))
            
            fun parse(t: String) = try { 
                if (t.count { it == ':' } == 1) sdf.parse("$t:00") else sdf.parse(t) 
            } catch (e: Exception) { null }

            val start = parse(startStr)
            val end = parse(endStr)

            if (start != null && end != null && now != null) {
                if (start.before(end)) now in start..end
                else now >= start || now <= end // Overnight
            } else false
        } catch (e: Exception) { false }
    }
}

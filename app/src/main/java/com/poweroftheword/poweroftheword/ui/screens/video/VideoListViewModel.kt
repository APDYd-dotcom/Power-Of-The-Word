package com.poweroftheword.poweroftheword.ui.screens.video

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poweroftheword.poweroftheword.data.local.VideoLikeDao
import com.poweroftheword.poweroftheword.data.local.VideoViewDao
import com.poweroftheword.poweroftheword.domain.model.VideoItem
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import com.poweroftheword.poweroftheword.util.DeviceUtils
import com.poweroftheword.poweroftheword.util.ShareUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class VideoListViewModel(
    private val repository: ChurchRepository,
    private val dao: VideoLikeDao,
    private val videoViewDao: VideoViewDao,
    private val context: Context
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedType = MutableStateFlow<String?>(null)
    val selectedType: StateFlow<String?> = _selectedType.asStateFlow()

    private val _rawVideos = MutableStateFlow<List<VideoItem>>(emptyList())
    
    val filteredVideos: StateFlow<List<VideoItem>> =
        combine(
            _rawVideos, 
            _searchQuery, 
            _selectedType, 
            dao.getAllLikesFlow(),
            videoViewDao.getAllViewsFlow()
        ) { videos, query, type, localLikes, localViews ->
            val likeMap = localLikes.associateBy { it.videoId }
            val viewMap = localViews.associateBy { it.videoId }
            
            videos.map { video ->
                val localState = likeMap[video.id.toString()]
                val localView = viewMap[video.id.toString()]
                
                video.copy(
                    isLiked = localState?.isLiked ?: false,
                    isViewed = localView != null,
                    like = if (localState?.isPending == true) {
                        if (localState.isLiked) (video.like ?: 0) + 1 else maxOf(0, (video.like ?: 0) - 1)
                    } else video.like,
                    views = if (localView?.isPending == true) {
                        (video.views ?: 0) + 1
                    } else video.views
                )
            }.filter { video ->
                val matchesQuery = query.isBlank() || 
                                  video.title.contains(query, true) || 
                                  video.description?.contains(query, true) == true
                val matchesType = type == null || video.type.equals(type, true)
                
                matchesQuery && matchesType
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val viewedVideoIds: StateFlow<Set<String>> = repository.getVideoViewedFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    init {
        repository.getSavedLanguage()
            .onEach { loadVideos() }
            .launchIn(viewModelScope)
    }

    val currentLanguage: StateFlow<String> = repository.getSavedLanguage()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "EN")

    fun onLanguageChange(lang: String) {
        viewModelScope.launch {
            repository.saveLanguage(lang)
            loadVideos()
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onTypeSelect(type: String?) {
        _selectedType.value = type
    }

    fun loadVideos() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val language = repository.getSavedLanguage().first()
                val result = repository.getVideos(language)
                _rawVideos.value = result
                
                val deviceId = DeviceUtils.getDeviceId(context)
                repository.syncMissingLikes(result, deviceId)
                
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load videos"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onVideoViewed(videoId: String) {
        viewModelScope.launch {
            // 1. Record locally first - this triggers the Flow in filteredVideos immediately
            repository.recordVideoViewLocal(videoId)
            
            // 2. Then call the interactions API in the background
            try {
                val deviceId = DeviceUtils.getDeviceId(context)
                repository.interactions(
                    deviceId = deviceId,
                    videoId = videoId,
                    action = "view"
                )
            } catch (e: Exception) {
                Log.e("VideoListVM", "Failed to sync view to server: ${e.message}")
            }
        }
    }

    fun likeVideo(videoId: String) {
        viewModelScope.launch {
            val deviceId = DeviceUtils.getDeviceId(context)
            repository.toggleVideoLikeLocal(videoId, deviceId)
        }
    }

    fun shareVideo(video: VideoItem) {
        viewModelScope.launch {
            val deviceId = DeviceUtils.getDeviceId(context)
            try {
                repository.interactions(
                    deviceId = deviceId,
                    videoId = video.id.toString(),
                    action = "share"
                )
                repository.shareVideo(video.id.toString(), deviceId)
            } catch (e: Exception) {
                Log.e("VideoListVM", "Failed to record share on server", e)
            }
            ShareUtils.shareText(context, "Check out this sermon:\n${video.title}\n${video.url}")
        }
    }
}

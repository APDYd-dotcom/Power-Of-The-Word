package com.poweroftheword.poweroftheword.ui.screens.video

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poweroftheword.poweroftheword.domain.model.VideoItem
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import com.poweroftheword.poweroftheword.util.DeviceUtils
import com.poweroftheword.poweroftheword.util.ShareUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class VideoListViewModel @Inject constructor(
    private val repository: ChurchRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedType = MutableStateFlow<String?>(null)
    val selectedType: StateFlow<String?> = _selectedType.asStateFlow()

    private val _videos = MutableStateFlow<List<VideoItem>>(emptyList())
    
    // Track liked IDs locally to simulate YouTube behavior
    private val _likedVideoIds = MutableStateFlow<Set<String>>(emptySet())

    val filteredVideos: StateFlow<List<VideoItem>> =
        combine(_videos, _searchQuery, _selectedType, _likedVideoIds) { videos, query, type, likedIds ->
            videos.map { video ->
                video.copy(isLiked = likedIds.contains(video.id.toString()))
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

    init {
        repository.getSavedLanguage()
            .onEach { loadVideos() }
            .launchIn(viewModelScope)
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
                
                val deviceId = DeviceUtils.getDeviceId(context)
                
                // Fetch liked status for each video in parallel
                val likedIds = result.map { video ->
                    async {
                        try {
                            if (repository.chackLike(deviceId, videoId = video.id.toString()).success.fanta) {
                                video.id.toString()
                            } else null
                        } catch (e: Exception) {
                            Log.e("VideoListViewModel", "Failed to check like status for ${video.id}", e)
                            null
                        }
                    }
                }.awaitAll().filterNotNull().toSet()
                
                _likedVideoIds.value = likedIds
                _videos.value = result
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load videos"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onVideoViewed(videoId: String) {
        viewModelScope.launch {
            val deviceId = DeviceUtils.getDeviceId(context)
            try {
                repository.recordVideoView(videoId, deviceId)
                Log.d("VideoListViewModel", "View recorded for video $videoId")
            } catch (e: Exception) {
                Log.e("VideoListViewModel", "Failed to record view", e)
            }
        }
    }

    fun likeVideo(videoId: String) {
        val isCurrentlyLiked = _likedVideoIds.value.contains(videoId)
        val currentVideos = _videos.value
        val videoIndex = currentVideos.indexOfFirst { it.id.toString() == videoId }
        
        if (videoIndex != -1) {
            val video = currentVideos[videoIndex]
            
            // Optimistic UI Update
            if (isCurrentlyLiked) {
                _likedVideoIds.value = _likedVideoIds.value - videoId
                val updatedVideo = video.copy(
                    like = maxOf(0, (video.like ?: 0) - 1), 
                    isLiked = false
                )
                val updatedList = currentVideos.toMutableList()
                updatedList[videoIndex] = updatedVideo
                _videos.value = updatedList
            } else {
                _likedVideoIds.value = _likedVideoIds.value + videoId
                val updatedVideo = video.copy(
                    like = (video.like ?: 0) + 1, 
                    isLiked = true
                )
                val updatedList = currentVideos.toMutableList()
                updatedList[videoIndex] = updatedVideo
                _videos.value = updatedList
            }
        }

        viewModelScope.launch {
            val deviceId = DeviceUtils.getDeviceId(context)
            try {
                // Use "like" action for both liking and unliking if the API toggles, 
                // or you might need a specific action if the API requires it.
                repository.interactions(deviceId, videoId = videoId, action = "like")
            } catch (e: Exception) {
                // Rollback on failure
                _likedVideoIds.value = if (isCurrentlyLiked) _likedVideoIds.value + videoId else _likedVideoIds.value - videoId
                _videos.value = currentVideos
                _error.value = "Failed to update like status."
                Log.e("VideoListViewModel", "Failed to toggle like", e)
            }
        }
    }

    fun shareVideo(video: VideoItem) {
        viewModelScope.launch {
            val deviceId = DeviceUtils.getDeviceId(context)
            try { repository.shareVideo(video.id.toString(), deviceId) } catch (_: Exception) {}
            ShareUtils.shareText(context, "Check out this sermon:\n${video.title}\n${video.url}")
        }
    }
}
package com.poweroftheword.poweroftheword.ui.screens.video

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poweroftheword.poweroftheword.domain.model.Video
import com.poweroftheword.poweroftheword.domain.model.VideoItem
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import com.poweroftheword.poweroftheword.util.DeviceUtils
import com.poweroftheword.poweroftheword.util.ShareUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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
    val video: StateFlow<List<VideoItem>> = _videos

    val filteredVideos: StateFlow<List<VideoItem>> =
        combine(_videos, _searchQuery) { videos, query ->
            if (query.isBlank()) {
                videos
            } else {
                videos.filter {
                    it.title.contains(query, true) ||
                            it.description?.contains(query, true) == true
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadVideos()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onTypeSelect(type: String?) {
        _selectedType.value = if (_selectedType.value == type) null else type
        loadVideos()
    }
    init {
        combine(_selectedType, flowOf(Unit)) { type, _ -> type }
            .onEach { loadVideos() }
            .launchIn(viewModelScope)
    }

    fun loadVideos() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val language = repository.getSavedLanguage().first()

                val result = repository.getVideos(
                    language = language,
                    type = _selectedType.value
                )

                _videos.value = result
                Log.d("VideoListViewModel", "Loaded ${result.size} videos")

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load videos"
                Log.e("VideoListViewModel", "Failed to load videos", e)
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
            } catch (e: Exception) {
                // Silent fail for analytics
            }
        }
    }

    fun likeVideo(videoId: String) {
        viewModelScope.launch {
            val deviceId = DeviceUtils.getDeviceId(context)
            try {
                repository.likeVideo(videoId, deviceId)
                // Refresh list to update like count if backend supports it
                loadVideos()
            } catch (e: Exception) {
                _error.value = "Failed to like video."
            }
        }
    }

    fun shareVideo(video: VideoItem) {
        viewModelScope.launch {
            val deviceId = DeviceUtils.getDeviceId(context)

            try {
                repository.shareVideo(video.id, deviceId)
            } catch (_: Exception) {}

            ShareUtils.shareText(
                context,
                "Check out this sermon:\n${video.title}\n${video.url}"
            )
        }
    }

    fun clearError() {
        _error.value = null
    }
}

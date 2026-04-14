package com.poweroftheword.poweroftheword.ui.screens.video

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poweroftheword.poweroftheword.data.local.VideoLikeDao
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
    private val dao: VideoLikeDao,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedType = MutableStateFlow<String?>(null)
    val selectedType: StateFlow<String?> = _selectedType.asStateFlow()

    private val _rawVideos = MutableStateFlow<List<VideoItem>>(emptyList())
    
    val filteredVideos: StateFlow<List<VideoItem>> =
        combine(_rawVideos, _searchQuery, _selectedType, dao.getAllLikesFlow()) { videos, query, type, localLikes ->
            val likeMap = localLikes.associateBy { it.videoId }
            videos.map { video ->
                val localState = likeMap[video.id.toString()]
                video.copy(
                    isLiked = localState?.isLiked ?: false,
                    like = if (localState?.isPending == true) {
                        if (localState.isLiked) (video.like ?: 0) + 1 else maxOf(0, (video.like ?: 0) - 1)
                    } else video.like
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
        viewModelScope.launch {
            val deviceId = DeviceUtils.getDeviceId(context)
            repository.toggleVideoLikeLocal(videoId, deviceId)
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

package com.poweroftheword.poweroftheword.ui.screens.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poweroftheword.poweroftheword.domain.model.*
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import com.poweroftheword.poweroftheword.util.DeviceUtils
import com.poweroftheword.poweroftheword.util.ShareUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val dailyWord: DailyWord? = null,
    val liveStreams: List<Live> = emptyList(),
    val latestVideos: List<VideoItem> = emptyList(),
    val latestFeeds: List<FeedItem> = emptyList(),
    val radioStatus: Radio? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentLanguage: String = "EN"
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ChurchRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        repository.getSavedLanguage()
            .onEach { lang ->
                _state.update { it.copy(currentLanguage = lang) }
                loadHomeData()
            }
            .launchIn(viewModelScope)
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
                val liveStreams = repository.getLiveStreams()
                val latestVideos = repository.getVideos(language)
                val latestFeeds = repository.getFeeds(language)

                _state.update {
                    it.copy(
                        dailyWord = DailyWord(dailyWordItems),
                        liveStreams = liveStreams,
                        latestVideos = latestVideos,
                        latestFeeds = latestFeeds,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Failed to load updates") }
            }
        }
    }

    fun likeVideo(videoId: String) {
        viewModelScope.launch {
            val deviceId = DeviceUtils.getDeviceId(context)
            try {
                Log.d("HomeViewModel", "Liking video: $videoId")
                repository.likeVideo(videoId, deviceId)
                loadHomeData()
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to like video.") }
            }
        }
    }

    fun shareVideo(video: VideoItem) {
        viewModelScope.launch {
            val deviceId = DeviceUtils.getDeviceId(context)
            try {
                Log.d("HomeViewModel", "Sharing video: ${video.id}")
                repository.shareVideo(video.id.toString(), deviceId)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Failed to record share on server", e)
            }

            ShareUtils.shareText(
                context,
                "Check out this sermon:\n${video.title}\n${video.url}"
            )
        }
    }
}

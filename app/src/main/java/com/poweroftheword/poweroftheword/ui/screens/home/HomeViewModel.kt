package com.poweroftheword.poweroftheword.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poweroftheword.poweroftheword.domain.model.*
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ✅ USE FeedItem (NOT Feed)
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
    private val repository: ChurchRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        // Observe language changes and reload data
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
            // loadHomeData() will be triggered by the flow observer in init
        }
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val language = repository.getSavedLanguage().first()

                val dailyWordItems = repository.getDailyWord(language)
                val liveStreams = repository.getLiveStreams()
                val latestVideos: List<VideoItem> = repository.getVideos(language)
                val latestFeeds = repository.getFeeds(language)
                val radioStatus = repository.getRadioStatus()

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

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load updates"
                    )
                }
            }
        }
    }
}
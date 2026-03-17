package com.poweroftheword.poweroftheword.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poweroftheword.poweroftheword.domain.model.*
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val dailyWord: DailyWord? = null,
    val liveStreams: List<Live> = emptyList(),
    val latestVideos: List<Video> = emptyList(),
    val latestFeeds: List<Feed> = emptyList(),
    val radioStatus: Radio? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ChurchRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                // Get the current language preference first
                val language = repository.getSavedLanguage().first()
                
                val dailyWord = repository.getDailyWord(language)
                val liveStreams = repository.getLiveStreams()
                val latestVideos = repository.getVideos(language).take(5)
                val latestFeeds = repository.getFeeds(language).take(3)
                val radioStatus = repository.getRadioStatus()

                _state.update {
                    it.copy(
                        dailyWord = dailyWord,
                        liveStreams = liveStreams,
                        latestVideos = latestVideos,
                        latestFeeds = latestFeeds,
                        radioStatus = radioStatus,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Failed to load updates") }
            }
        }
    }
}

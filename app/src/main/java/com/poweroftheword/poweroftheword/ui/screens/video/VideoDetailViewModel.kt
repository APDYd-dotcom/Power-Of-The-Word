package com.poweroftheword.poweroftheword.ui.screens.video

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poweroftheword.poweroftheword.domain.model.Video
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VideoDetailState(
    val video: Video? = null,
    val relatedVideos: List<Video> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class VideoDetailViewModel @Inject constructor(
    private val repository: ChurchRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val videoId: String? = savedStateHandle["videoId"]

    private val _state = MutableStateFlow(VideoDetailState())
    val state: StateFlow<VideoDetailState> = _state.asStateFlow()

    init {
        loadVideoDetail()
    }

    fun loadVideoDetail() {
        val id = videoId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val language = repository.getSavedLanguage().first()
                val allVideos = repository.getVideos(language)
                val video = allVideos.find { it.id == id }
                val related = allVideos.filter { it.id != id }.take(5)

                _state.update {
                    it.copy(
                        video = video,
                        relatedVideos = related,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Failed to load video details") }
            }
        }
    }
}

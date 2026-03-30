package com.poweroftheword.poweroftheword.ui.screens.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poweroftheword.poweroftheword.domain.model.FeedItem
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: ChurchRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FeedState())
    val state: StateFlow<FeedState> = _state.asStateFlow()

    // Compatibility properties for other observers
    private val _feeds = MutableStateFlow<List<FeedItem>>(emptyList())
    val feeds: StateFlow<List<FeedItem>> = _feeds.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadFeeds()
    }

    fun loadFeeds() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            _isLoading.value = true

            try {
                val language = repository.getSavedLanguage().first()

                // ✅ Use runCatching to enable the use of .fold() on the repository call result
                runCatching {
                    repository.getFeeds(language)
                }.fold(
                    onSuccess = { feedsList ->
                        _state.update {
                            it.copy(
                                feeds = feedsList,
                                isLoading = false,
                                error = null
                            )
                        }
                        _feeds.value = feedsList
                        _isLoading.value = false
                        Log.d("FeedVM", "Loaded ${feedsList} feeds")
                    },
                    onFailure = { error ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = error.message ?: "Something went wrong"
                            )
                        }
                        _isLoading.value = false
                        Log.e("FeedVM", "Error: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to initialize"
                    )
                }
                Log.e("FeedVM", "Error: ${e.message}")
                _isLoading.value = false
            }
        }
    }
}

data class FeedState(
    val feeds: List<FeedItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

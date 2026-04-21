package com.poweroftheword.poweroftheword.ui.screens.feed

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poweroftheword.poweroftheword.data.local.FeedLikeDao
import com.poweroftheword.poweroftheword.domain.model.FeedItem
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import com.poweroftheword.poweroftheword.util.DeviceUtils
import com.poweroftheword.poweroftheword.util.ShareUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: ChurchRepository,
    private val feedLikeDao: FeedLikeDao,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(FeedState())
    val state: StateFlow<FeedState> = _state.asStateFlow()

    private val _feeds = MutableStateFlow<List<FeedItem>>(emptyList())
    val feeds: StateFlow<List<FeedItem>> = _feeds.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val currentLanguage: StateFlow<String> = repository.getSavedLanguage()
        .stateIn(viewModelScope, SharingStarted.Eagerly, "EN")

    val likedFeedIds: StateFlow<Set<String>> = feedLikeDao.getAllLikesFlow()
        .map { likes -> likes.filter { it.isLiked }.map { it.feedId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    init {
        viewModelScope.launch {
            currentLanguage.collect {
                loadFeeds()
            }
        }
    }

    fun onLanguageChange(language: String) {
        viewModelScope.launch {
            repository.saveLanguage(language)
        }
    }

    fun loadFeeds() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            _isLoading.value = true

            try {
                val language = repository.getSavedLanguage().first()

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
                        
                        val deviceId = DeviceUtils.getDeviceId(context)
                        repository.syncMissingFeedLikes(feedsList, deviceId)
                        
                        Log.d("FeedVM", "Loaded ${feedsList.size} feeds")
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

    fun onFeedViewed(feedId: String) {
        viewModelScope.launch {
            try {
                val deviceId = DeviceUtils.getDeviceId(context)
                repository.interactions(
                    deviceId = deviceId,
                    feedId = feedId,
                    action = "view"
                )
            } catch (e: Exception) {
                Log.e("FeedVM", "Failed to register feed view: ${e.message}")
            }
        }
    }

    fun toggleLike(feedId: String) {
        viewModelScope.launch {
            val deviceId = DeviceUtils.getDeviceId(context)
            repository.toggleFeedLikeLocal(feedId, deviceId)
        }
    }

    fun shareFeed(feed: FeedItem) {
        viewModelScope.launch {
            val deviceId = DeviceUtils.getDeviceId(context)
            try {
                repository.interactions(
                    deviceId = deviceId,
                    feedId = feed.id.toString(),
                    action = "share"
                )
            } catch (e: Exception) {
                Log.e("FeedVM", "Failed to register feed share: ${e.message}")
            }
            ShareUtils.shareText(
                context,
                "Check out this event: ${feed.title}\n${feed.desc ?: ""}"
            )
        }
    }
}

data class FeedState(
    val feeds: List<FeedItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

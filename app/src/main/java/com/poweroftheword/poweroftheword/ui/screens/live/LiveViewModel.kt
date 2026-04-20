package com.poweroftheword.poweroftheword.ui.screens.live

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poweroftheword.poweroftheword.domain.model.LiveItem
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import com.poweroftheword.poweroftheword.util.DeviceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LiveViewModel @Inject constructor(
    private val repository: ChurchRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _liveStreams = MutableStateFlow<List<LiveItem>>(emptyList())
    val liveStreams: StateFlow<List<LiveItem>> = _liveStreams.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadLiveStreams()
    }

    private fun loadLiveStreams() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val language = repository.getSavedLanguage().first()
                val result = repository.getLiveStreams(language)
                _liveStreams.value = result
                Log.d("LiveViewModel", "Loaded ${result.size} live streams")
            } catch (e: Exception) {
                Log.e("LiveViewModel", "Failed to load live streams", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onLiveClicked(liveId: Int) {
        viewModelScope.launch {
            val deviceId = DeviceUtils.getDeviceId(context)
            repository.registerLiveViewer(liveId, deviceId)
        }
    }
}

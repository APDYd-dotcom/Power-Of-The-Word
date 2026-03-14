package com.poweroftheword.poweroftheword.ui.screens.live

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poweroftheword.poweroftheword.domain.model.Live
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import com.poweroftheword.poweroftheword.util.DeviceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LiveViewModel @Inject constructor(
    private val repository: ChurchRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _liveStreams = MutableStateFlow<List<Live>>(emptyList())
    val liveStreams: StateFlow<List<Live>> = _liveStreams.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadLiveStreams()
    }

    private fun loadLiveStreams() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getLiveStreams()
                _liveStreams.value = result
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onLiveClicked(liveId: String) {
        viewModelScope.launch {
            val deviceId = DeviceUtils.getDeviceId(context)
            repository.registerLiveViewer(liveId, deviceId)
        }
    }
}

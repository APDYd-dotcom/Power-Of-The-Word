package com.poweroftheword.poweroftheword.ui.screens.radio

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.poweroftheword.poweroftheword.domain.model.Program
import com.poweroftheword.poweroftheword.domain.model.Radio
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RadioViewModel @Inject constructor(
    private val repository: ChurchRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _radioStatus = MutableStateFlow<Radio?>(null)
    val radioStatus: StateFlow<Radio?> = _radioStatus.asStateFlow()

    private val _programs = MutableStateFlow<List<Program>>(emptyList())
    val programs: StateFlow<List<Program>> = _programs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentlyPlayingId = MutableStateFlow<String?>(null)
    val currentlyPlayingId: StateFlow<String?> = _currentlyPlayingId.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val exoPlayer = ExoPlayer.Builder(application).build()

    init {
        loadRadioData()
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }
        })
    }

    fun loadRadioData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _radioStatus.value = repository.getRadioStatus()
                _programs.value = repository.getPrograms()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun togglePlay(radio: Radio) {
        if (_currentlyPlayingId.value == radio.id) {
            if (_isPlaying.value) {
                exoPlayer.pause()
            } else {
                exoPlayer.play()
            }
        } else {
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
            val mediaItem = MediaItem.fromUri(radio.streamUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
            _currentlyPlayingId.value = radio.id
        }
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
    }
}

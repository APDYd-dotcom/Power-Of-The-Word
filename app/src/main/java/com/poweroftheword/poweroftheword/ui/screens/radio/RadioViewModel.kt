package com.poweroftheword.poweroftheword.ui.screens.radio

import android.app.Application
import android.util.Log
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.collections.emptyList

@HiltViewModel
class RadioViewModel @Inject constructor(
    private val repository: ChurchRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _radioStatus = MutableStateFlow<List<Radio>>(emptyList())
    val radioStatus: StateFlow<List<Radio>> = _radioStatus.asStateFlow()

    private val _programs = MutableStateFlow<List<Program>>(emptyList())
    val programs: StateFlow<List<Program>> = _programs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentlyPlayingId = MutableStateFlow<Int?>(null)
    val currentlyPlayingId: StateFlow<Int?> = _currentlyPlayingId.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val exoPlayer = ExoPlayer.Builder(application).build()

    init {
        loadRadioData()
        startActiveStatusTicker()
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }
        })
    }

    private fun startActiveStatusTicker() {
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(60000) // Update every minute
                _radioStatus.value = _radioStatus.value.map { radio ->
                    radio.copy(isActive = isRadioCurrentlyActive(radio))
                }
            }
        }
    }

    fun playById(id: Int) {
        viewModelScope.launch {
            // Ensure radio data is loaded first if not already
            if (_radioStatus.value.isEmpty()) {
                val rawRadios = repository.getRadioStatus()
                _radioStatus.value = rawRadios.map { radio ->
                    radio.copy(isActive = isRadioCurrentlyActive(radio))
                }
            }
            
            val radio = _radioStatus.value.find { it.id == id }
            if (radio != null) {
                if (_currentlyPlayingId.value != radio.id) {
                    exoPlayer.stop()
                    exoPlayer.clearMediaItems()
                    val mediaItem = MediaItem.fromUri(radio.url)
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.prepare()
                    exoPlayer.play()
                    _currentlyPlayingId.value = radio.id
                } else if (!_isPlaying.value) {
                    exoPlayer.play()
                }
            }
        }
    }

    fun loadRadioData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val language = repository.getSavedLanguage().first()
                val rawRadios = repository.getRadioStatus()
                _radioStatus.value = rawRadios.map { radio ->
                    radio.copy(isActive = isRadioCurrentlyActive(radio))
                }
                Log.e("RadioViewModel", "Radio Status: ${_radioStatus.value}")
                _programs.value = repository.getPrograms(language)
            } catch (e: Exception) {
                Log.e("RadioViewModel Error", "Error loading radio data", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun isRadioCurrentlyActive(radio: Radio): Boolean {
        return try {
            val calendar = Calendar.getInstance()
            // Get current day abbreviation in English (e.g., "Mon", "Sun")
            val currentDay = SimpleDateFormat("EEE", Locale.ENGLISH).format(calendar.time).lowercase()

            // 1. Check if today is a broadcasting day
            if (radio.days.isNotEmpty()) {
                val isBroadcastingToday = radio.days.any { it.equals(currentDay, ignoreCase = true) }
                if (!isBroadcastingToday) return false
            }

            // 2. Check if current time is within broadcasting hours
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val nowStr = sdf.format(calendar.time)
            val now = sdf.parse(nowStr)
            
            val start = sdf.parse(radio.startHour)
            val end = sdf.parse(radio.endHour)

            if (start != null && end != null && now != null) {
                if (start.before(end)) {
                    // Normal range (e.g., 08:00 - 20:00)
                    now in start..end
                } else {
                    // Overnight range (e.g., 22:00 - 04:00)
                    now >= start || now <= end
                }
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("RadioViewModel", "Error parsing time or day", e)
            false
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
            val mediaItem = MediaItem.fromUri(radio.url)
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

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
import com.poweroftheword.poweroftheword.domain.model.RadioItem
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.collections.emptyList

class RadioViewModel(
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
                val rawRadios = repository.getRadioStatus()
                _radioStatus.value = rawRadios
                    .groupBy { it.radio?.id }
                    .mapNotNull { (_, items) ->
                        val firstItem = items.firstOrNull() ?: return@mapNotNull null
                        val radio = firstItem.radio ?: return@mapNotNull null
                        radio.copy(
                            isActive = items.any { isRadioCurrentlyActive(it) }
                        )
                    }
            }
        }
    }

    fun playById(id: Int) {
        viewModelScope.launch {
            // Ensure radio data is loaded first if not already
            if (_radioStatus.value.isEmpty()) {
                val rawRadios = repository.getRadioStatus()
                _radioStatus.value = rawRadios
                    .groupBy { it.radio?.id }
                    .mapNotNull { (_, items) ->
                        val firstItem = items.firstOrNull() ?: return@mapNotNull null
                        val radio = firstItem.radio ?: return@mapNotNull null
                        radio.copy(
                            isActive = items.any { isRadioCurrentlyActive(it) }
                        )
                    }
            }
            
            val radio = _radioStatus.value.find { it.id == id }
            if (radio != null) {
                if (_currentlyPlayingId.value != radio.id) {
                    exoPlayer.stop()
                    exoPlayer.clearMediaItems()
                    val mediaItem = MediaItem.fromUri(radio.url ?: "")
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
                _radioStatus.value = rawRadios
                    .groupBy { it.radio?.id }
                    .mapNotNull { (_, items) ->
                        val firstItem = items.firstOrNull() ?: return@mapNotNull null
                        val radio = firstItem.radio ?: return@mapNotNull null
                        radio.copy(
                            isActive = items.any { isRadioCurrentlyActive(it) }
                        )
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

    private fun isRadioCurrentlyActive(radioItem: RadioItem): Boolean {
        val startStr = radioItem.startHour?.takeIf { it.isNotBlank() } ?: return false
        val endStr = radioItem.endHour?.takeIf { it.isNotBlank() } ?: return false
        val day = radioItem.day?.takeIf { it.isNotBlank() } ?: return false

        return try {
            val calendar = Calendar.getInstance()
            val currentDay = SimpleDateFormat("EEE", Locale.US).format(calendar.time).lowercase()
            
            // 1. Strict Day Check
            if (!day.equals(currentDay, ignoreCase = true)) return false

            // 2. Strict Time Check
            val sdf = SimpleDateFormat("HH:mm:ss", Locale.US)
            val now = sdf.parse(sdf.format(calendar.time))
            
            fun parse(t: String) = try { 
                if (t.count { it == ':' } == 1) sdf.parse("$t:00") else sdf.parse(t) 
            } catch (e: Exception) { null }

            val start = parse(startStr)
            val end = parse(endStr)

            if (start != null && end != null && now != null) {
                if (start.before(end)) now in start..end
                else now >= start || now <= end // Overnight
            } else false
        } catch (e: Exception) { false }
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
            val mediaItem = MediaItem.fromUri(radio.url ?: "")
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

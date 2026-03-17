package com.poweroftheword.poweroftheword.ui.screens.audio

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poweroftheword.poweroftheword.domain.model.Audio
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import com.poweroftheword.poweroftheword.util.DeviceUtils
import com.poweroftheword.poweroftheword.util.ShareUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioListViewModel @Inject constructor(
    private val repository: ChurchRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _audios = MutableStateFlow<List<Audio>>(emptyList())
    
    val filteredAudios: StateFlow<List<Audio>> = combine(_audios, _searchQuery) { audios, query ->
        if (query.isBlank()) {
            audios
        } else {
            audios.filter { it.title.contains(query, ignoreCase = true) || it.date.contains(query) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadAudios()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    private fun loadAudios() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val language = repository.getSavedLanguage().first()
                val result = repository.getAudioSermons(language)
                _audios.value = result
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onAudioListened(audioId: String) {
        viewModelScope.launch {
            val deviceId = DeviceUtils.getDeviceId(context)
            repository.recordAudioListen(audioId, deviceId)
        }
    }

    fun likeAudio(audioId: String) {
        viewModelScope.launch {
            val deviceId = DeviceUtils.getDeviceId(context)
            repository.likeAudio(audioId, deviceId)
        }
    }

    fun shareAudio(audio: Audio) {
        viewModelScope.launch {
            val deviceId = DeviceUtils.getDeviceId(context)
            repository.shareAudio(audio.id, deviceId)
            ShareUtils.shareText(context, "Listen to this sermon: ${audio.title}\n${audio.audioUrl}")
        }
    }
}

package com.poweroftheword.poweroftheword.ui.screens.audio

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poweroftheword.poweroftheword.domain.model.Audio
import com.poweroftheword.poweroftheword.domain.model.AudioItem
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

    private val _audios = MutableStateFlow<List<AudioItem>>(emptyList())
    val audios: StateFlow<List<AudioItem>> = _audios.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    //  FILTERED LIST
    val filteredAudios: StateFlow<List<AudioItem>> =
        combine(_audios, _searchQuery) { audios, query ->
            if (query.isBlank()) {
                audios
            } else {
                audios.filter {
                    it.title.contains(query, true) ||
                            it.date.contains(query)
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadAudios()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun loadAudios() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val language = repository.getSavedLanguage().first()
                val result = repository.getAudio(language)

                _audios.value = result.flatMap { listOf(it) }

                Log.d("AudioVM", "Loaded: $result")

            } catch (e: Exception) {
                Log.e("AudioVM", "Error", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onAudioListened(audioId: Int) {
        viewModelScope.launch {
            val deviceId = DeviceUtils.getDeviceId(context)
            repository.recordAudioListen(audioId, deviceId)
        }
    }

    fun likeAudio(audioId: Int) {
        viewModelScope.launch {
            val deviceId = DeviceUtils.getDeviceId(context)
            repository.likeAudio(audioId, deviceId)
        }
    }

    fun shareAudio(audio: AudioItem) {
        viewModelScope.launch {
            val deviceId = DeviceUtils.getDeviceId(context)

            repository.shareAudio(audio.id, deviceId)

            ShareUtils.shareText(
                context,
                "Listen to this sermon: ${audio.title}\n${audio.file}"
            )
        }
    }
}
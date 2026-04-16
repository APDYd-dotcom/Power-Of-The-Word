package com.poweroftheword.poweroftheword.ui.screens.audio

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poweroftheword.poweroftheword.data.local.AudioLikeDao
import com.poweroftheword.poweroftheword.domain.model.AudioItem
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import com.poweroftheword.poweroftheword.util.DeviceUtils
import com.poweroftheword.poweroftheword.util.ShareUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AudioListViewModel @Inject constructor(
    private val repository: ChurchRepository,
    private val audioLikeDao: AudioLikeDao,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _audios = MutableStateFlow<List<AudioItem>>(emptyList())
    val audios: StateFlow<List<AudioItem>> = _audios.asStateFlow()

    val likedAudioIds: StateFlow<Set<String>> = audioLikeDao.getAllLikesFlow()
        .map { likes -> likes.filter { it.isLiked }.map { it.audioId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH))
    val selectedMonth: StateFlow<Int> = _selectedMonth.asStateFlow()

    private val _selectedYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val filteredAudios: StateFlow<List<AudioItem>> =
        combine(_audios, _searchQuery, _selectedMonth, _selectedYear) { audios, query, month, year ->
            val monthName = getMonthName(month)
            audios.filter { audio ->
                val matchesQuery = query.isBlank() || audio.title.contains(query, true)
                val matchesDate = audio.date.contains(year.toString()) && 
                                 (audio.date.contains(monthName, true) || audio.date.contains(String.format("%02d", month + 1)))
                matchesQuery && matchesDate
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        repository.getSavedLanguage()
            .onEach { loadAudios() }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onDateChange(month: Int, year: Int) {
        _selectedMonth.value = month
        _selectedYear.value = year
    }

    fun loadAudios() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val language = repository.getSavedLanguage().first()
                val result = repository.getAudio(language)
                _audios.value = result
                
                val deviceId = DeviceUtils.getDeviceId(context)
                repository.syncMissingAudioLikes(result, deviceId)

            } catch (e: Exception) {
                Log.e("AudioVM", "Error", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getMonthName(month: Int): String {
        return when (month) {
            0 -> "January" 1 -> "February" 2 -> "March" 3 -> "April"
            4 -> "May" 5 -> "June" 6 -> "July" 7 -> "August"
            8 -> "September" 9 -> "October" 10 -> "November" 11 -> "December"
            else -> ""
        }
    }

    fun onAudioListened(audioId: Int) {
        viewModelScope.launch {
            try {
                val deviceId = DeviceUtils.getDeviceId(context)
                // Register view via interaction API
                repository.interactions(
                    deviceId = deviceId,
                    audioId = audioId.toString(),
                    action = "view"
                )
                // Also call the legacy recordAudioListen
                repository.recordAudioListen(audioId, deviceId)
            } catch (e: Exception) {
                Log.e("AudioVM", "Failed to register audio view: ${e.message}")
            }
        }
    }

    fun toggleLike(audioId: String) {
        viewModelScope.launch {
            val deviceId = DeviceUtils.getDeviceId(context)
            repository.toggleAudioLikeLocal(audioId, deviceId)
        }
    }

    fun shareAudio(audio: AudioItem) {
        viewModelScope.launch {
            val deviceId = DeviceUtils.getDeviceId(context)
            try {
                repository.interactions(deviceId, audioId = audio.id.toString(), action = "share")
            } catch (e: Exception) {
                Log.e("AudioVM", "Failed to register share: ${e.message}")
            }
            ShareUtils.shareText(
                context,
                "Listen to this sermon: ${audio.title}\n${audio.file}"
            )
        }
    }
}

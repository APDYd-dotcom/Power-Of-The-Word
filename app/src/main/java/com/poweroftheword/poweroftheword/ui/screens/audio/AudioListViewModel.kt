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
import com.poweroftheword.poweroftheword.util.formatDate
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import com.poweroftheword.poweroftheword.util.download.AudioDownloadManager
import com.poweroftheword.poweroftheword.util.download.DownloadProgress
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AudioListViewModel @Inject constructor(
    private val repository: ChurchRepository,
    private val audioLikeDao: AudioLikeDao,
    val downloadManager: AudioDownloadManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _audios = MutableStateFlow<List<AudioItem>>(emptyList())
    val audios: StateFlow<List<AudioItem>> = _audios.asStateFlow()

    private val _downloadedAudioIds = MutableStateFlow<Set<Int>>(emptySet())
    val downloadedAudioIds: StateFlow<Set<Int>> = _downloadedAudioIds.asStateFlow()

    private val _downloadProgress = MutableStateFlow<Map<Int, Float>>(emptyMap())
    val downloadProgress: StateFlow<Map<Int, Float>> = _downloadProgress.asStateFlow()

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
            val monthNumber = String.format("%02d", month + 1)
            
            audios.filter { audio ->
                val matchesQuery = query.isBlank() || audio.title.contains(query, true)
                
                // The issue was likely matching partial strings or incorrect month logic.
                // We should match the year AND either the full month name or the month number (MM)
                // specifically in a way that doesn't conflict (e.g., "03" shouldn't match "2023" unless it's the month).
                val matchesYear = audio.date.contains(year.toString())
                val matchesMonth = audio.date.contains(monthName, true) || audio.date.contains("-$monthNumber-") || audio.date.contains("/$monthNumber/")
                
                matchesQuery && matchesYear && matchesMonth
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        refreshDownloadedList()
        repository.getSavedLanguage()
            .onEach { loadAudios() }
            .launchIn(viewModelScope)
    }

    private fun refreshDownloadedList() {
        _audios.value.let { list ->
            _downloadedAudioIds.value = list.filter { downloadManager.isAudioDownloaded(it.id) }
                .map { it.id }.toSet()
        }
        
        // Also check files directly in case audios haven't loaded yet
        viewModelScope.launch {
            val directory = File(context.filesDir, "audios")
            if (directory.exists()) {
                val ids = directory.listFiles()
                    ?.mapNotNull { file ->
                        file.name.removePrefix("audio_").removeSuffix(".mp3").toIntOrNull()
                    }?.toSet() ?: emptySet()
                _downloadedAudioIds.value = ids
            }
        }
    }

    fun downloadAudio(audio: AudioItem) {
        viewModelScope.launch {
            // Set initial progress to 0 to show the loading indicator immediately
            _downloadProgress.value = _downloadProgress.value + (audio.id to 0f)

            downloadManager.downloadAudio(audio.id, "https://poweroftheword.bi${audio.file}")
                .collect { progress ->
                    when (progress) {
                        is DownloadProgress.Running -> {
                            _downloadProgress.value = _downloadProgress.value + (audio.id to progress.progress)
                        }
                        is DownloadProgress.Success -> {
                            _downloadedAudioIds.value = _downloadedAudioIds.value + audio.id
                            _downloadProgress.value = _downloadProgress.value - audio.id
                        }
                        is DownloadProgress.Error -> {
                            _downloadProgress.value = _downloadProgress.value - audio.id
                            Log.e("AudioVM", "Download error: ${progress.message}")
                        }
                    }
                }
        }
    }

    fun shareDownloadedAudio(audio: AudioItem) {
        viewModelScope.launch {
            val deviceId = DeviceUtils.getDeviceId(context)
            try {
                repository.interactions(deviceId, audioId = audio.id.toString(), action = "share")
            } catch (e: Exception) {
                Log.e("AudioVM", "Failed to register share: ${e.message}")
            }
            
            val file = downloadManager.getAudioFile(audio.id)
            if (file.exists()) {
                ShareUtils.shareAudioInTwoParts(
                    context,
                    file,
                    audio.title,
                    "Power Of The Word\n${audio.title}\nDate: ${formatDate(audio.date)}"
                )
            } else {
                shareAudio(audio)
            }
        }
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
                "Power Of The Word\n${audio.title}\nDate: ${formatDate(audio.date)}\nListen here: https://poweroftheword.bi${audio.file}"
            )
        }
    }
}

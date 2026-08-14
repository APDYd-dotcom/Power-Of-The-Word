package com.poweroftheword.poweroftheword.ui.screens.dailyword

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poweroftheword.poweroftheword.domain.model.DailyWord
import com.poweroftheword.poweroftheword.domain.model.DailyWordItem
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DailyWordViewModel(
    private val repository: ChurchRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DailyWordState())
    val state: StateFlow<DailyWordState> = _state.asStateFlow()

    val currentLanguage: StateFlow<String> = repository.getSavedLanguage()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "EN")

    init {
        currentLanguage
            .onEach { loadDailyWord() }
            .launchIn(viewModelScope)
    }

    private fun loadDailyWord() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val language = repository.getSavedLanguage().first()
                val result: List<DailyWordItem> = repository.getDailyWord(language)
                
                _state.update {
                    it.copy(
                        data = result,
                        isLoading = false,
                        error = null
                    )
                }
                Log.d("DailyWordViewModel", "Loaded daily word: ${result.size}")
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Something went wrong"
                    )
                }
                Log.e("DailyWordViewModel", "Error loading daily word", e)
            }
        }
    }
}

data class DailyWordState(
    val data: List<DailyWordItem>? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

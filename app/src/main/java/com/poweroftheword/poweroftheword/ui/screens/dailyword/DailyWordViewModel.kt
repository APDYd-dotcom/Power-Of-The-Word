package com.poweroftheword.poweroftheword.ui.screens.dailyword

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poweroftheword.poweroftheword.domain.model.DailyWord
import com.poweroftheword.poweroftheword.domain.model.DailyWordItem
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyWordViewModel @Inject constructor(
    private val repository: ChurchRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DailyWordState())
    val state: StateFlow<DailyWordState> = _state.asStateFlow()

    init {
        loadDailyWord()
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

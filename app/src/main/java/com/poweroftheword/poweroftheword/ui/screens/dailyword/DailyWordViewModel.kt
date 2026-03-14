package com.poweroftheword.poweroftheword.ui.screens.dailyword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poweroftheword.poweroftheword.domain.model.DailyWord
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyWordViewModel @Inject constructor(
    private val repository: ChurchRepository
) : ViewModel() {

    private val _dailyWord = MutableStateFlow<DailyWord?>(null)
    val dailyWord: StateFlow<DailyWord?> = _dailyWord.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadDailyWord()
    }

    private fun loadDailyWord() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getSavedLanguage().collectLatest { language ->
                try {
                    val result = repository.getDailyWord(language)
                    _dailyWord.value = result
                } catch (e: Exception) {
                    // Handle error
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }
}

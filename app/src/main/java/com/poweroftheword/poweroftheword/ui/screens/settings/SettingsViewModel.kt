package com.poweroftheword.poweroftheword.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: ChurchRepository
) : ViewModel() {

    val currentLanguage: StateFlow<String> = repository.getSavedLanguage()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "EN"
        )

    val isDarkMode: StateFlow<Boolean?> = repository.getSavedTheme()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun setLanguage(language: String) {
        viewModelScope.launch {
            repository.saveLanguage(language)
        }
    }

    fun setTheme(isDark: Boolean?) {
        viewModelScope.launch {
            repository.saveTheme(isDark)
        }
    }
}

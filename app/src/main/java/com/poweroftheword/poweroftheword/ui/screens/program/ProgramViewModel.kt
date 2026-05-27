package com.poweroftheword.poweroftheword.ui.screens.program

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poweroftheword.poweroftheword.domain.model.Program
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProgramViewModel(
    private val repository: ChurchRepository
) : ViewModel() {

    private val _programs = MutableStateFlow<List<Program>>(emptyList())
    val programs: StateFlow<List<Program>> = _programs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadPrograms()
    }

    fun loadPrograms() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val language = repository.getSavedLanguage().first()
                _programs.value = repository.getPrograms(language)
                Log.e("ProgramViewModel", "Programs loaded successfully, _programs.value: ${_programs.value}")
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to load programs"
                Log.e("ProgramViewModel", "Error loading programs", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}

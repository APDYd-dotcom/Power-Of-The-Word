package com.poweroftheword.poweroftheword.ui.screens.radio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poweroftheword.poweroftheword.domain.model.Program
import com.poweroftheword.poweroftheword.domain.model.Radio
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RadioViewModel @Inject constructor(
    private val repository: ChurchRepository
) : ViewModel() {

    private val _radioStatus = MutableStateFlow<Radio?>(null)
    val radioStatus: StateFlow<Radio?> = _radioStatus.asStateFlow()

    private val _programs = MutableStateFlow<List<Program>>(emptyList())
    val programs: StateFlow<List<Program>> = _programs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadRadioData()
    }

    fun loadRadioData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _radioStatus.value = repository.getRadioStatus()
                _programs.value = repository.getPrograms()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}

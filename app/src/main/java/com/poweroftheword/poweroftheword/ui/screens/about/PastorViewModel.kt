package com.poweroftheword.poweroftheword.ui.screens.about

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poweroftheword.poweroftheword.domain.model.Pastor
import com.poweroftheword.poweroftheword.domain.model.PastorItem
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PastorViewModel(
    private val repository: ChurchRepository
) : ViewModel() {
    private val _pastor = MutableStateFlow<List<PastorItem>>(emptyList())
    val pastor: StateFlow<List<PastorItem>> = _pastor

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadPastor()
    }

    fun loadPastor() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _pastor.value = listOf(repository.getPastor().first())
                _error.value = null
                Log.e("PastorViewModel", "Pastor loaded successfully, _pastor.value: ${_pastor.value}")
            } catch (e: Exception) {
                _error.value = "Failed to load pastor"
                Log.e("PastorViewModel", "Error loading pastor", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
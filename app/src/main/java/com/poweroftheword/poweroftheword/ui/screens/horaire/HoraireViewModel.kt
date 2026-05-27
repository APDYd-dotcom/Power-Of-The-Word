package com.poweroftheword.poweroftheword.ui.screens.horaire

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poweroftheword.poweroftheword.domain.model.Horaire
import com.poweroftheword.poweroftheword.domain.model.HoraireItem
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HoraireViewModel(
    private val repository: ChurchRepository
) : ViewModel() {

    private val _horaires = MutableStateFlow<List<HoraireItem>>(emptyList())
    val horaires: StateFlow<List<HoraireItem>> = _horaires.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadHoraires()
    }

    private fun loadHoraires() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getSavedLanguage().collectLatest { language ->
                try {
                    val result = repository.getHoraire(language)
                    _horaires.value = result
                    Log.d("HoraireViewModel", "Loaded ${result.size} horaires")
                } catch (e: Exception) {
                    // Handle error
                    Log.e("HoraireViewModel", "Error loading horaires", e)
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }
}

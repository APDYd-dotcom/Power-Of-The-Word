package com.poweroftheword.poweroftheword.ui.screens.horaire

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poweroftheword.poweroftheword.domain.model.Horaire
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HoraireViewModel @Inject constructor(
    private val repository: ChurchRepository
) : ViewModel() {

    private val _horaires = MutableStateFlow<List<Horaire>>(emptyList())
    val horaires: StateFlow<List<Horaire>> = _horaires.asStateFlow()

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
                } catch (e: Exception) {
                    // Handle error
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }
}

package com.poweroftheword.poweroftheword.ui.screens.about

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poweroftheword.poweroftheword.domain.model.SocialMediaItem
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SocialMediaViewModel(
    private val churchRepository: ChurchRepository
) : ViewModel() {
    private val _socialMedia = MutableStateFlow<List<SocialMediaItem>>(emptyList())
    val socialMedia: StateFlow<List<SocialMediaItem>> = _socialMedia 
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    init {
        loadSocialMedia()
    }
    
    fun loadSocialMedia() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _socialMedia.value = churchRepository.getSocialMedia()
                Log.d("SocialMediaViewModel", "Social media loaded successfully")
            } catch (e: Exception) {
                Log.e("SocialMediaViewModel", "Error loading social media", e) 
            } finally {
                _isLoading.value = false
            }
        } 
    }
}

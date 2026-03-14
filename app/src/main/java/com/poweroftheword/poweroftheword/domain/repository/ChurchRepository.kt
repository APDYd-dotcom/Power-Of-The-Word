package com.poweroftheword.poweroftheword.domain.repository

import com.poweroftheword.poweroftheword.domain.model.DailyWord
import com.poweroftheword.poweroftheword.domain.model.Video
import kotlinx.coroutines.flow.Flow

interface ChurchRepository {
    suspend fun getVideos(language: String): List<Video>
    suspend fun getDailyWord(language: String): DailyWord?
    
    fun getSavedLanguage(): Flow<String>
    suspend fun saveLanguage(language: String)
}

package com.poweroftheword.poweroftheword.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DailyWord(
    val id: String,
    val content: String,
    val imageUrl: String,
    val date: String,
    val language: String
)

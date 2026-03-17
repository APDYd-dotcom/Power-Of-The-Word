package com.poweroftheword.poweroftheword.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DailyWord(
    val id: String,
    val content: String,
    @SerialName("image_url")
    val imageUrl: String,
    val date: String,
    val language: String
)

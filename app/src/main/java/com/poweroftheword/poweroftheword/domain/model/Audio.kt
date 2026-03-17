package com.poweroftheword.poweroftheword.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Audio(
    val id: String,
    val title: String,
    @SerialName("audio_url")
    val audioUrl: String,
    val date: String,
    val language: String,
    val listens: Int = 0,
    val likes: Int = 0,
    val shares: Int = 0
)

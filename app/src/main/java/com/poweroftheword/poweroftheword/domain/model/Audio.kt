package com.poweroftheword.poweroftheword.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Audio(
    val audios: List<AudioItem>
)

@Serializable
data class AudioItem(
    val id: Int,
    val title: String,
    val file: String,
    val language: String,
    val date: String,
    @SerialName("visible_date") val visibleDate: String?,
    @SerialName("visible_time") val visibleTime: String?,
    val status: String,
    val listens: Int? = 0,
    val likes: Int? = 0,
    val isLiked: Boolean? = false,
    val shares: Int? = 0
)

package com.poweroftheword.poweroftheword.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Video(
    val videos: List<VideoItem>
)

@Serializable
data class VideoItem(
    val id: Int,
    val title: String,
    val description: String? = "Power Of The Word",
    val url: String,
    val type: String,
    val language: String,
    val view: Int? = 0,
    val share: Int? = 0,
    val like: Int? = 0,
    val date: String,
    val isLiked: Boolean = false
)

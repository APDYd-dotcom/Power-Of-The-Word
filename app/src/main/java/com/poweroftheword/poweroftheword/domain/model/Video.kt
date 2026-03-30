package com.poweroftheword.poweroftheword.domain.model

import com.google.common.reflect.TypeResolver
import kotlinx.serialization.SerialName
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
    val views: Int? = 0,
    val likes: Int? = 0,
    val date: String
)

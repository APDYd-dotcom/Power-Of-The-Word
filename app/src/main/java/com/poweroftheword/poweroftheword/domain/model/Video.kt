package com.poweroftheword.poweroftheword.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Video(
    val id: String,
    val title: String,
    val description: String,
    @SerialName("thumbnail_url")
    val thumbnailUrl: String,
    @SerialName("video_url")
    val videoUrl: String,
    val language: String,
    val views: Int = 0,
    val likes: Int = 0,
    @SerialName("date_published")
    val datePublished: String
)

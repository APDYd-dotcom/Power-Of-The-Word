package com.poweroftheword.poweroftheword.domain.model

import com.google.common.reflect.TypeResolver
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Video(
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    val results: List<VideoItem>
)

@Serializable
data class VideoItem(
    val id: String,
    val title: String,
    val description: String?,
    val url: String,
    val type: String,
    val language: String,
    val views: Int? = 0,
    val likes: Int? = 0,
    val date: String
)

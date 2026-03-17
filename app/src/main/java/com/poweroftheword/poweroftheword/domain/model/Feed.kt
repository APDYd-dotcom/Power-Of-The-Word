package com.poweroftheword.poweroftheword.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Feed(
    val id: String,
    val title: String,
    val description: String,
    @SerialName("image_url")
    val imageUrl: String?,
    val language: String,
    val type: String // igikorane, itaganzo
)

package com.poweroftheword.poweroftheword.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Feed(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val language: String,
    val type: String // igikorane, itaganzo
)

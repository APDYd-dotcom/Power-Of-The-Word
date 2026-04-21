package com.poweroftheword.poweroftheword.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SocialMedia (
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    val results: List<SocialMediaItem>
)

@Serializable
data class SocialMediaItem (
    val id: Int,
    val name: String,
    val logo: String,
    val url: String,
    @SerialName("created_at")
    val createdAt: String
)
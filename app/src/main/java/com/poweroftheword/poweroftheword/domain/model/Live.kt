package com.poweroftheword.poweroftheword.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Live(
    val id: String,
    val title: String,
    @SerialName("stream_url")
    val streamUrl: String,
    @SerialName("is_active")
    val isActive: Boolean,
    val viewers: Int = 0
)

package com.poweroftheword.poweroftheword.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Live(
    val id: String,
    val title: String,
    val streamUrl: String,
    val isActive: Boolean,
    val viewers: Int = 0
)

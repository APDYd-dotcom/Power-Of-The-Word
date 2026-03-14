package com.poweroftheword.poweroftheword.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Radio(
    val id: String,
    val name: String,
    val streamUrl: String,
    val startHour: String,
    val endHour: String,
    val isActive: Boolean
)

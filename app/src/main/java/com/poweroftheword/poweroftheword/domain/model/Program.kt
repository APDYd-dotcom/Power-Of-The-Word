package com.poweroftheword.poweroftheword.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Program(
    val id: String,
    val title: String,
    val description: String,
    val day: String,
    val startHour: String,
    val endHour: String
)

package com.poweroftheword.poweroftheword.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DailyWord(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<DailyWordItem>
)

@Serializable
data class DailyWordItem(
    val id: Int,
    val photo: String,
    val language: String,
    val date: String,
)

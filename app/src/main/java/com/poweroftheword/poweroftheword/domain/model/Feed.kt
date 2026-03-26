package com.poweroftheword.poweroftheword.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Feed(
    val id: String,
    val title: String,
    val photo: String,
    val language: String,
    val type: String,
    val date: String,
//    val description: String?
)

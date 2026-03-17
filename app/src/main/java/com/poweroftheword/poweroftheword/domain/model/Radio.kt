package com.poweroftheword.poweroftheword.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Radio(
    val id: String,
    val name: String,
    @SerialName("stream_url")
    val streamUrl: String,
    @SerialName("start_hour")
    val startHour: String,
    @SerialName("end_hour")
    val endHour: String,
    @SerialName("is_active")
    val isActive: Boolean
)

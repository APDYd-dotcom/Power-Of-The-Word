package com.poweroftheword.poweroftheword.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RadioResponse(
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    val results: List<Radio>
)

@Serializable
data class Radio(
    val id: Int,
    val name: String,
    val url: String,
    @SerialName("start_hour")
    val startHour: String,
    @SerialName("end_hour")
    val endHour: String,
    @SerialName("is_active")
    val isActive: Boolean
)
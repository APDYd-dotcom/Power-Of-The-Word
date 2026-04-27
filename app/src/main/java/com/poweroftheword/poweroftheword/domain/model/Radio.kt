package com.poweroftheword.poweroftheword.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RadioResponse(
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    val results: List<RadioItem>
)

@Serializable
data class RadioItem(
    val id: Int,
    val radio : Radio,
    val day : String,
    @SerialName("start_hour")
    val startHour: String,
    @SerialName("end_hour")
    val endHour: String,
)
@Serializable
data class Radio(
    val id: Int,
    val name: String,
    val photo: String,
    val url: String,
    @SerialName("is_active")
    val isActive: Boolean,
)
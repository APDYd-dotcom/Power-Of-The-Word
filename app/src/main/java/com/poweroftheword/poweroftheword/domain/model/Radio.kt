package com.poweroftheword.poweroftheword.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RadioResponse(
    val count: Int = 0,
    val next: String? = null,
    val previous: String? = null,
    val results: List<RadioItem> = emptyList()
)

@Serializable
data class RadioItem(
    val id: Int? = null,
    val radio : Radio? = null,
    val day : String? = "",
    @SerialName("start_hour")
    val startHour: String? = "",
    @SerialName("end_hour")
    val endHour: String? = "",
)

@Serializable
data class Radio(
    val id: Int? = null,
    val name: String? = "Power Of The Word Radio",
    val photo: String? = null,
    val url: String? = "",
    @SerialName("is_active")
    val isActive: Boolean = false,
)

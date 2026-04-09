package com.poweroftheword.poweroftheword.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProgramResponse(
    val programs: List<Program>
)

@Serializable
data class Program(
    val id: Int,
    val title: String,
    val description: String,
    val day: String,
    @SerialName("start_hour")
    val startHour: String,
    @SerialName("end_hour")
    val endHour: String
)

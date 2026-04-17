package com.poweroftheword.poweroftheword.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Horaire(
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    val results: List<HoraireItem>
)
@Serializable
data class HoraireItem(
    val id: Int,
    val day: String,
    @SerialName("full_name")
    val name: String,
    val language: String,
    @SerialName("number")
    val contactNumber: String,
    val date: String,
)

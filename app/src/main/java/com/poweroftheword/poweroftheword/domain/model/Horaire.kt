package com.poweroftheword.poweroftheword.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Horaire(
    val id: String,
    val name: String,
    @SerialName("contact_number")
    val contactNumber: String,
    val availability: String,
    val language: String
)

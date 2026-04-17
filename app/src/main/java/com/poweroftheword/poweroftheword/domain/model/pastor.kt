package com.poweroftheword.poweroftheword.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pastor(
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    val results: List<PastorItem>
)

@Serializable
data class PastorItem(
    val id: Int,
    @SerialName("full_name")
    val fullName: String,
    val photo: String,
    val phone: String,
    val email: String,
    @SerialName("whatsapp_number")
    val whatsapp: String,
    val bio: String,
    @SerialName("created_at")
    val createdAt: String,
)
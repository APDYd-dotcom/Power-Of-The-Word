package com.poweroftheword.poweroftheword.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class InteractionResponse(
    val success: Boolean,
    val message: String,
    val data: String
)
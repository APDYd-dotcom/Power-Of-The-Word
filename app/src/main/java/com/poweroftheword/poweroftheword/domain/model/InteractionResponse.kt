package com.poweroftheword.poweroftheword.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class InteractionResponse(
    val success: Fanta,
    val message: String,
    val data: String
)
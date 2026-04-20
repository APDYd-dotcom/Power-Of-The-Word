package com.poweroftheword.poweroftheword.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.intellij.lang.annotations.Language

@Serializable
data class Live(
//    val count: Int,
//    val next: String? = null,
//    val previous: String? = null,
    val lives: List<LiveItem>
)
@Serializable
data class LiveItem(
    val id: Int,
    val title: String,
    val thumbnail: String,
    @SerialName("url")
    val streamUrl: String,
    val language: String,
    @SerialName("is_active")
    val isActive: Boolean,
    val view: Int = 0,
    val like: Int = 0,
    val share: Int = 0,
    val date : String
)

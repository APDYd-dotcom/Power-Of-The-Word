package com.poweroftheword.poweroftheword.domain.model
import kotlinx.serialization.Serializable

@Serializable
data class Feed(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<FeedItem>
)

@Serializable
data class FeedItem(
    val id: Int,
    val title: String,
    val photo: String? = null,
    val language: String,
    val type: String,
    val desc: String,
    val date: String
)
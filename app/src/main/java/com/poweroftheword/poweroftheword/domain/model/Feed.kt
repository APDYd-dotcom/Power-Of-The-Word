package com.poweroftheword.poweroftheword.domain.model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Feed(
//    val count: Int,
//    val next: String?,
//    val previous: String?,
    val feeds: List<FeedItem>
)

@Serializable
data class FeedItem(
    val id: Int,
    val title: String,
    val photo: String? = null,
    val language: String,
    val type: String,
    val desc: String,
    val views: Int,
    val like: Int,
    val share: Int,
    @SerialName("start_hour")
    val startHour: String,
    @SerialName("end_hour")
    val endHour: String,
    val location: String,
    val host: String,
    val expectation: String,
    @SerialName("created_at")
    val createdAt: String,
    val date: String
)
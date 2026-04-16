package com.poweroftheword.poweroftheword.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Feed(
//    val count: Int = 0,
//    val next: String? = null,
//    val previous: String? = null,
    val feeds: List<FeedItem>
)

@Serializable
data class FeedItem(
    val id: Int,
    val title: String,
    val photo: String? = null,
    val language: String? = null,
    val type: String? = null,
    val desc: String? = null,
    @SerialName("view")
    val views: Int = 0,
    val like: Int = 0,
    val share: Int = 0,
    @SerialName("start_hour")
    val startHour: String? = null,
    @SerialName("end_hour")
    val endHour: String? = null,
    @SerialName("lacation")
    val location: String? = null,
    val host: String? = null,
    val expectation: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    val date: String? = null
)

package com.dotsphoto.api.controllers.dto

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionApiDto(
    val id: Long,
    val planId: Long,
    val dateFrom: LocalDateTime,
    val dateTo: LocalDateTime
)
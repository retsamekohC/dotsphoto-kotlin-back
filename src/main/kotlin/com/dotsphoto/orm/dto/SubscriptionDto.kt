package com.dotsphoto.orm.dto

import com.dotsphoto.orm.tables.Subscription
import com.dotsphoto.orm.util.CreateLongDto
import com.dotsphoto.orm.util.LongIdTableDto
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionDto(
    override val id: Long,
    val planId: Long,
    val dateFrom: LocalDateTime,
    val dateTo: LocalDateTime
) : LongIdTableDto<Subscription.Table>

data class CreateSubscriptionDto(
    val planId: Long,
    val dateFrom: LocalDateTime,
    val dateTo: LocalDateTime
) : CreateLongDto<Subscription.Table>
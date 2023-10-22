package com.dotsphoto.orm.dto

import com.dotsphoto.orm.enums.Statuses
import com.dotsphoto.orm.tables.SubscriptionPlan
import com.dotsphoto.orm.util.CreateLongDto
import com.dotsphoto.orm.util.LongIdTableDto

data class SubscriptionPlanDto(
    override val id: Long,
    val planName: String,
    val availableSpaceGb: Int,
    val periodMonths: Int,
    val priceCop: Int,
    val status: Statuses
) : LongIdTableDto<SubscriptionPlan.Table>

data class CreateSubscriptionPlanDto(
    val planName: String,
    val availableSpaceGb: Int,
    val periodMonths: Int,
    val priceCop: Int
) : CreateLongDto<SubscriptionPlan.Table>
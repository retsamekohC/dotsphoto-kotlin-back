package com.dotsphoto.orm.services.repositories

import com.dotsphoto.orm.dto.CreateSubscriptionPlanDto
import com.dotsphoto.orm.dto.SubscriptionPlanDto
import com.dotsphoto.orm.tables.SubscriptionPlan
import org.jetbrains.exposed.sql.ResultRow

class SubscriptionPlanRepository : NonUpdatableLongIdRepository<SubscriptionPlan.Table, SubscriptionPlanDto, CreateSubscriptionPlanDto>() {
    override fun create(cdto: CreateSubscriptionPlanDto): SubscriptionPlanDto = insertAndGetDto {
        it[planName] = cdto.planName
        it[availableSpaceGb] = cdto.availableSpaceGb
        it[periodMonths] = cdto.periodMonths
        it[priceCop] = cdto.priceCop
    }

    override fun mapper(resultRow: ResultRow): SubscriptionPlanDto = SubscriptionPlanDto(
        resultRow[SubscriptionPlan.id].value,
        resultRow[SubscriptionPlan.planName],
        resultRow[SubscriptionPlan.availableSpaceGb],
        resultRow[SubscriptionPlan.periodMonths],
        resultRow[SubscriptionPlan.priceCop],
        resultRow[SubscriptionPlan.status],
    )

    override fun getTable(): SubscriptionPlan.Table = SubscriptionPlan.Table
}
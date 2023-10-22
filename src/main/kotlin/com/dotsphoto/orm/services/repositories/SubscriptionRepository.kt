package com.dotsphoto.orm.services.repositories

import com.dotsphoto.orm.dto.CreateSubscriptionDto
import com.dotsphoto.orm.dto.SubscriptionDto
import com.dotsphoto.orm.tables.Subscription
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert

class SubscriptionRepository : NonUpdatableLongIdRepository<Subscription.Table, SubscriptionDto, CreateSubscriptionDto>() {
    override fun create(cdto: CreateSubscriptionDto): SubscriptionDto = insertAndGetDto {
        it[plan] = cdto.planId
        it[dateFrom] = cdto.dateFrom
        it[dateTo] = cdto.dateTo
    }

    override fun mapper(resultRow: ResultRow): SubscriptionDto = SubscriptionDto(
        resultRow[Subscription.id].value,
        resultRow[Subscription.plan].value,
        resultRow[Subscription.dateFrom],
        resultRow[Subscription.dateTo],
    )

    override fun getTable(): Subscription.Table = Subscription.Table
}
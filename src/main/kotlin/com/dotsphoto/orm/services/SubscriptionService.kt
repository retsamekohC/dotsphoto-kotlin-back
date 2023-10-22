package com.dotsphoto.orm.services

import com.dotsphoto.orm.dto.CreateSubscriptionDto
import com.dotsphoto.orm.dto.SubscriptionDto
import com.dotsphoto.orm.dto.SubscriptionPlanDto
import com.dotsphoto.orm.services.repositories.SubscriptionRepository
import com.dotsphoto.orm.tables.Subscription
import com.dotsphoto.utils.DateUtils
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.atTime
import kotlinx.datetime.plus

class SubscriptionService(repository: SubscriptionRepository): LongIdService<Subscription.Table, SubscriptionDto, CreateSubscriptionDto>(repository) {

    fun createSubscription(plan: SubscriptionPlanDto):SubscriptionDto {
        if (plan.id == null) throw IllegalArgumentException("subscription plan does not exists, plan=$plan")
        val dateStart = DateUtils.now()
        val dateEnd = DateUtils.now().date.plus(DatePeriod(months = plan.periodMonths!!)).atTime(dateStart.time)
        return repository.create(CreateSubscriptionDto(plan.id, dateStart, dateEnd))
    }
}
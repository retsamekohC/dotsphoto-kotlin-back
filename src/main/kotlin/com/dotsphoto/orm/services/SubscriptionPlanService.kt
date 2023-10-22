package com.dotsphoto.orm.services

import com.dotsphoto.orm.dto.CreateSubscriptionPlanDto
import com.dotsphoto.orm.dto.SubscriptionPlanDto
import com.dotsphoto.orm.services.repositories.SubscriptionPlanRepository
import com.dotsphoto.orm.tables.SubscriptionPlan

class SubscriptionPlanService(repository: SubscriptionPlanRepository) :
    LongIdService<SubscriptionPlan.Table, SubscriptionPlanDto, CreateSubscriptionPlanDto>(repository) {
    fun getSimpleSubscriptionPlan(): SubscriptionPlanDto {
        return repository.findUnique { SubscriptionPlan.planName eq "basic_free" }
            ?: repository.create(CreateSubscriptionPlanDto(planName = "basic_free", 10, 999, 0))
    }
}
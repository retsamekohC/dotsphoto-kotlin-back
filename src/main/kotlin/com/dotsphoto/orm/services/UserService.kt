package com.dotsphoto.orm.services

import com.dotsphoto.orm.dto.CreateUserDto
import com.dotsphoto.orm.dto.UpdateUserDto
import com.dotsphoto.orm.dto.UserDto
import com.dotsphoto.orm.services.repositories.UserRepository
import com.dotsphoto.orm.tables.User
import org.koin.java.KoinJavaComponent.inject

class UserService(repository: UserRepository) : LongIdDaoService<User.Table, UserDto, CreateUserDto, UpdateUserDto>(repository) {

    private val albumService by inject<AlbumService>(AlbumService::class.java)
    private val subscriptionService by inject<SubscriptionService>(SubscriptionService::class.java)
    private val subscriptionPlanService by inject<SubscriptionPlanService>(SubscriptionPlanService::class.java)

    fun registerUser(email: String, nickname: String?, fullName: String?): UserDto {
        return repository.create(CreateUserDto(
            nickname = nickname ?: email.substringBefore("@"),
            email = email,
            fullName = fullName,
            rootAlbumId = albumService.createAlbum(nickname ?: email.substringBefore("@")).id,
            subscriptionId = subscriptionService.createSubscription(subscriptionPlanService.getSimpleSubscriptionPlan()).id
        ))
    }

    fun findByEmail(email: String): UserDto? {
        return repository.findUnique { User.email eq email }
    }

}
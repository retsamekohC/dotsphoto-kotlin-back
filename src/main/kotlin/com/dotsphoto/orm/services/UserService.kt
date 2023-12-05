package com.dotsphoto.orm.services

import com.dotsphoto.orm.dto.CreateUserDto
import com.dotsphoto.orm.dto.UpdateUserDto
import com.dotsphoto.orm.dto.UserDto
import com.dotsphoto.orm.services.repositories.UserRepository
import com.dotsphoto.orm.tables.User
import com.dotsphoto.orm.util.Utils
import org.koin.java.KoinJavaComponent.inject
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.pbkdf2.SHA256Digest
import java.math.BigInteger
import java.security.MessageDigest

class UserService(repository: UserRepository) : LongIdDaoService<User.Table, UserDto, CreateUserDto, UpdateUserDto>(repository) {

    private val albumService by inject<AlbumService>(AlbumService::class.java)
    private val subscriptionService by inject<SubscriptionService>(SubscriptionService::class.java)
    private val subscriptionPlanService by inject<SubscriptionPlanService>(SubscriptionPlanService::class.java)
    private val ownershipService by inject<OwnershipService>(OwnershipService::class.java)

    fun registerUser(nickname: String, credentials: String): UserDto {
        val rootAlbum = albumService.createAlbum(nickname ?: nickname)
        val user = repository.create(CreateUserDto(
            nickname = nickname,
            rootAlbumId = rootAlbum.id,
            subscriptionId = subscriptionService.createSubscription(subscriptionPlanService.getSimpleSubscriptionPlan()).id,
            userCreds = credentials
        ))
        ownershipService.createOwnershipOwner(user.id, rootAlbum.id)
        return user
    }

    /**
     * Ищет пользователя по хэшу данных авторизации
     * @param creds SHA1 хекс от base64 строки формата login:password
     */
    fun findByCreds(creds: String): UserDto? {
        return repository.findUnique { User.userCreds eq creds }
    }

    fun getAllUsers(): List<UserDto> {
        return repository.findAll()
    }

    fun getUsersWithAccessTo(albumId: Long): List<UserDto> {
        return (repository as UserRepository).getUsersWithAccessTo(albumId)
    }
}
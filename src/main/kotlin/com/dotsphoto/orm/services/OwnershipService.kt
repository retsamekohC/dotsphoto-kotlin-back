package com.dotsphoto.orm.services

import com.dotsphoto.api.controllers.dto.UserApiDto
import com.dotsphoto.orm.dto.AlbumDto
import com.dotsphoto.orm.dto.CreateOwnerhshipDto
import com.dotsphoto.orm.dto.OwnershipDto
import com.dotsphoto.orm.dto.UserDto
import com.dotsphoto.orm.enums.OwnershipLevel
import com.dotsphoto.orm.services.repositories.OwnershipRepository
import com.dotsphoto.orm.tables.Ownership
import org.jetbrains.exposed.sql.and

class OwnershipService(repository: OwnershipRepository) : LongIdService<Ownership.Table, OwnershipDto, CreateOwnerhshipDto>(repository) {
    /**
     * Создает запись о полном владении альбомом для пользователяя
     */
    fun createOwnershipOwner(userId: Long, albumId: Long):OwnershipDto {
        return repository.create(CreateOwnerhshipDto(albumId, userId, OwnershipLevel.OWNER))
    }

    /**
     * Создает запись о ридонли доступе к альбому для пользователя
     */
    fun createOwnershipView(userId: Long, albumId: Long):OwnershipDto {
        return repository.create(CreateOwnerhshipDto(albumId, userId, OwnershipLevel.VIEW))
    }

    /**
     * true если права есть, false если нет
     */
    fun checkRights(albumId: Long, userId: Long) : Boolean {
        return repository.findUnique { Ownership.album eq albumId and (Ownership.user eq userId) } != null
    }

    /**
     * true если указанный пользователь - владелец альбома, false если нет
     */
    fun checkRightsOwner(albumId: Long, userId: Long) : Boolean {
        return repository.findUnique { Ownership.album eq albumId and (Ownership.user eq userId) and (Ownership.level eq OwnershipLevel.OWNER) } != null
    }


}
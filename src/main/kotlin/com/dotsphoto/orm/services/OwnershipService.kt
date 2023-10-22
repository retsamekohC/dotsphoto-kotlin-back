package com.dotsphoto.orm.services

import com.dotsphoto.orm.dto.AlbumDto
import com.dotsphoto.orm.dto.CreateOwnerhshipDto
import com.dotsphoto.orm.dto.OwnershipDto
import com.dotsphoto.orm.dto.UserDto
import com.dotsphoto.orm.enums.OwnershipLevel
import com.dotsphoto.orm.services.repositories.OwnershipRepository
import com.dotsphoto.orm.tables.Ownership
import org.jetbrains.exposed.sql.and

class OwnershipService(repository: OwnershipRepository) : LongIdService<Ownership.Table, OwnershipDto, CreateOwnerhshipDto>(repository) {
    fun createOwnershipOwner(userDto: UserDto, albumDto: AlbumDto):OwnershipDto {
        return repository.create(CreateOwnerhshipDto(albumDto.id, userDto.id, OwnershipLevel.OWNER))
    }

    fun checkRights(albumId: Long, userId: Long) : Boolean {
        return repository.findUnique { Ownership.album eq albumId and (Ownership.user eq userId) } == null
    }
}
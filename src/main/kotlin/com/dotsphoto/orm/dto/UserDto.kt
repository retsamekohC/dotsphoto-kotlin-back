package com.dotsphoto.orm.dto

import com.dotsphoto.orm.enums.Statuses
import com.dotsphoto.orm.tables.User
import com.dotsphoto.orm.util.CreateLongDto
import com.dotsphoto.orm.util.LongIdTableDto
import com.dotsphoto.orm.util.UpdateLongDto
import kotlinx.serialization.Serializable

@Serializable

data class UserDto(
    override val id: Long,
    val nickname: String?,
    val email: String,
    val fullName: String?,
    val rootAlbumId: Long,
    val subscriptionId: Long,
    val status: Statuses
) : LongIdTableDto<User.Table>

data class CreateUserDto(
    val nickname: String,
    val email: String,
    val fullName: String?,
    val rootAlbumId: Long,
    val subscriptionId: Long
) : CreateLongDto<User.Table>

data class UpdateUserDto(
    val status: Statuses
) : UpdateLongDto<User.Table>
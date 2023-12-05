package com.dotsphoto.api.controllers.dto

import com.dotsphoto.orm.dto.UserDto
import com.dotsphoto.orm.enums.Statuses
import kotlinx.serialization.Serializable

@Serializable
data class UserApiDto(
    val id: Long,
    val nickname: String,
    val rootAlbumId: Long,
    val subscriptionId: Long,
    val status: Statuses
) {
    companion object {
        fun from(userDto: UserDto): UserApiDto = UserApiDto(
            userDto.id,
            userDto.nickname,
            userDto.rootAlbumId,
            userDto.subscriptionId,
            userDto.status
        )
    }
}
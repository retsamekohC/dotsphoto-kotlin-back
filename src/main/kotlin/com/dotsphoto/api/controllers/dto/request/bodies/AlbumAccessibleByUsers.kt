package com.dotsphoto.api.controllers.dto.request.bodies

import com.dotsphoto.api.controllers.dto.AlbumApiDto
import com.dotsphoto.api.controllers.dto.UserApiDto
import kotlinx.serialization.Serializable

@Serializable
data class AlbumAccessibleByUsers(val albumId: AlbumApiDto, val userIds: List<UserApiDto>)
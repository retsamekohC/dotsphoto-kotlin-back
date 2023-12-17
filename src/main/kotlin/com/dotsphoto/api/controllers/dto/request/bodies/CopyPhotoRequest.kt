package com.dotsphoto.api.controllers.dto.request.bodies

import kotlinx.serialization.Serializable

@Serializable
data class CopyPhotoRequest(val photoId: Long, val albumId: Long)

package com.dotsphoto.api.controllers.dto.request.bodies

import kotlinx.serialization.Serializable

@Serializable
data class ShareRequest(val userId: Long, val albumId: Long)
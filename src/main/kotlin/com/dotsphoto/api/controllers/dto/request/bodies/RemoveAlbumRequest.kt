package com.dotsphoto.api.controllers.dto.request.bodies

import kotlinx.serialization.Serializable

@Serializable
data class RemoveAlbumRequest(val albumId: Long)

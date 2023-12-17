package com.dotsphoto.api.controllers.dto.request.bodies

import kotlinx.serialization.Serializable

@Serializable
data class NewAlbumRequest(val albumName: String)

package com.dotsphoto.api.controllers.dto


import com.dotsphoto.orm.enums.OwnershipLevel
import kotlinx.serialization.Serializable

@Serializable
data class OwnershipApiDto(
    val id: Long,
    val albumId: Long,
    val userId: Long,
    val level: OwnershipLevel
)
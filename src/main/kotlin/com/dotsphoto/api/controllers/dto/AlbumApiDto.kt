package com.dotsphoto.api.controllers.dto

import com.dotsphoto.orm.dto.AlbumDto
import com.dotsphoto.orm.enums.Statuses
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class AlbumApiDto(
    val id: Long,
    val albumName: String,
    val createdAt: LocalDateTime,
    val lastUpdatedAt: LocalDateTime,
    val status: Statuses
) {
    companion object {
        fun from(albumDto: AlbumDto) = AlbumApiDto(
            albumDto.id,
            albumDto.albumName,
            albumDto.createdAt,
            albumDto.lastUpdatedAt,
            albumDto.status
        )
    }
}
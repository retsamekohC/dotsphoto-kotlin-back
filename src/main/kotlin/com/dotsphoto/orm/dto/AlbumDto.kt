package com.dotsphoto.orm.dto

import com.dotsphoto.api.controllers.dto.AlbumApiDto
import com.dotsphoto.orm.enums.Statuses
import com.dotsphoto.orm.tables.Album
import com.dotsphoto.orm.util.CreateLongDto
import com.dotsphoto.orm.util.LongIdTableDto
import com.dotsphoto.orm.util.UpdateLongDto
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class AlbumDto(
    override val id: Long,
    val albumName: String,
    val createdAt: LocalDateTime,
    val lastUpdatedAt: LocalDateTime,
    val status: Statuses
) : LongIdTableDto<Album.Table>

data class CreateAlbumDto(
    val albumName: String,
) : CreateLongDto<Album.Table>

data class UpdateAlbumDto(
    val status: Statuses
) : UpdateLongDto<Album.Table>
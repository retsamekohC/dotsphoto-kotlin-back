package com.dotsphoto.orm.dto

import com.dotsphoto.orm.enums.Statuses
import com.dotsphoto.orm.tables.Photo
import com.dotsphoto.orm.tables.PhotoContent
import com.dotsphoto.orm.util.CreateLongDto
import com.dotsphoto.orm.util.LongIdTableDto
import com.dotsphoto.orm.util.UpdateLongDto
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class PhotoDto(
    override val id: Long,
    val photoContentId: Long,
    val fileName: String,
    val createdAt: LocalDateTime,
    val lastUpdatedAt: LocalDateTime,
    val metadataId: Long?,
    val status: Statuses,
    val albumId: Long
) : LongIdTableDto<Photo.Table>

data class CreatePhotoDto(
    val photoContentId: Long,
    val fileName: String,
    val albumId: Long
) : CreateLongDto<Photo.Table>

data class UpdatePhotoDto(
    val status: Statuses
) : UpdateLongDto<Photo.Table>
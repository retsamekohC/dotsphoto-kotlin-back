package com.dotsphoto.orm.dto

import com.dotsphoto.orm.tables.PhotoMetadata
import com.dotsphoto.orm.util.CreateLongDto
import com.dotsphoto.orm.util.LongIdTableDto
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class PhotoMetadataDto(
    override val id: Long,
    val widthInPixels: Int?,
    val heightInPixels: Int?,
    val cameraMegapixels: Float?,
    val kilobyteSize: Int?,
    val geolocation: String?,
    val shotAt: LocalDateTime?
) : LongIdTableDto<PhotoMetadata.Table>

data class CreatePhotoMetadataDto(
    // TODO: расширить чтобы оно реально юзабельно было
    val kilobyteSize: Int
) : CreateLongDto<PhotoMetadata.Table>
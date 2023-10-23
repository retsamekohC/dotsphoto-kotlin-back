package com.dotsphoto.orm.dto

import com.dotsphoto.orm.enums.Statuses
import com.dotsphoto.orm.tables.Photo
import com.dotsphoto.orm.util.CreateLongDto
import com.dotsphoto.orm.util.LongIdTableDto
import com.dotsphoto.orm.util.UpdateLongDto
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class PhotoDto(
    override val id: Long,
    val content: ByteArray,
    val fileName: String,
    val createdAt: LocalDateTime,
    val lastUpdatedAt: LocalDateTime,
    val metadataId: Long?,
    val status: Statuses,
    val albumId: Long
) : LongIdTableDto<Photo.Table> {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PhotoDto

        if (content != null) {
            if (other.content == null) return false
            if (!content.contentEquals(other.content)) return false
        } else if (other.content != null) return false
        if (fileName != other.fileName) return false
        if (createdAt != other.createdAt) return false
        if (lastUpdatedAt != other.lastUpdatedAt) return false
        if (metadataId != other.metadataId) return false
        if (status != other.status) return false
        if (albumId != other.albumId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = content?.contentHashCode() ?: 0
        result = 31 * result + fileName.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + (lastUpdatedAt?.hashCode() ?: 0)
        result = 31 * result + metadataId.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + albumId.hashCode()
        return result
    }
}

data class CreatePhotoDto(
    val content: ByteArray,
    val fileName: String,
    val albumId: Long
) : CreateLongDto<Photo.Table> {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CreatePhotoDto

        if (!content.contentEquals(other.content)) return false
        if (fileName != other.fileName) return false
        if (albumId != other.albumId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = content.contentHashCode()
        result = 31 * result + fileName.hashCode()
        result = 31 * result + albumId.hashCode()
        return result
    }
}

data class UpdatePhotoDto(
    val status: Statuses
) : UpdateLongDto<Photo.Table>
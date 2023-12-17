package com.dotsphoto.orm.dto

import com.dotsphoto.orm.tables.PhotoContent
import com.dotsphoto.orm.util.CreateLongDto
import com.dotsphoto.orm.util.LongIdTableDto
import kotlinx.serialization.Serializable

@Serializable
data class PhotoContentDto(
    override val id: Long,
    val content: ByteArray,
    val compressedContent: ByteArray
) : LongIdTableDto<PhotoContent.Table> {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PhotoContentDto

        if (id != other.id) return false
        if (!content.contentEquals(other.content)) return false
        if (!compressedContent.contentEquals(other.compressedContent)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + content.contentHashCode()
        result = 31 * result + compressedContent.contentHashCode()
        return result
    }
}

data class CreatePhotoContentDto(
    val content: ByteArray,
) : CreateLongDto<PhotoContent.Table> {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CreatePhotoContentDto

        return content.contentEquals(other.content)
    }

    override fun hashCode(): Int {
        return content.contentHashCode()
    }
}
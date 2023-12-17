package com.dotsphoto.orm.services.repositories

import com.dotsphoto.orm.dto.CreatePhotoDto
import com.dotsphoto.orm.dto.PhotoDto
import com.dotsphoto.orm.dto.UpdatePhotoDto
import com.dotsphoto.orm.enums.Statuses
import com.dotsphoto.orm.tables.Photo
import com.dotsphoto.utils.DateUtils
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import java.awt.Image
import java.awt.image.BufferedImage
import java.awt.image.RenderedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam


class PhotoRepository : LongIdDaoRepository<Photo.Table, PhotoDto, CreatePhotoDto, UpdatePhotoDto>() {
    override fun create(cdto: CreatePhotoDto): PhotoDto = insertAndGetDto {
        it[contentId] = cdto.photoContentId
        it[fileName] = cdto.fileName
        it[album] = cdto.albumId
        it[createdAt] = DateUtils.now()
        it[lastUpdatedAt] = DateUtils.now()
    }

    override fun update(id: Long, udto: UpdatePhotoDto): PhotoDto? = updateById(id) {
        it[status] = udto.status
    }

    override fun mapper(resultRow: ResultRow): PhotoDto = PhotoDto(
        resultRow[Photo.id].value,
        resultRow[Photo.contentId].value,
        resultRow[Photo.fileName],
        resultRow[Photo.createdAt],
        resultRow[Photo.lastUpdatedAt],
        resultRow[Photo.metadata]?.value,
        resultRow[Photo.status],
        resultRow[Photo.album].value,
    )

    fun findByAlbumId(albumId: Long) : List<PhotoDto> {
        return findAll { Photo.album eq albumId and (Photo.status eq Statuses.ACTIVE) }
    }

    override fun getTable(): Photo.Table = Photo.Table
}
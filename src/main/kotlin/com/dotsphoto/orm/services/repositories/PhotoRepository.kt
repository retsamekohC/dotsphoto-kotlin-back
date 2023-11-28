package com.dotsphoto.orm.services.repositories

import com.dotsphoto.orm.dto.CreatePhotoDto
import com.dotsphoto.orm.dto.PhotoDto
import com.dotsphoto.orm.dto.UpdatePhotoDto
import com.dotsphoto.orm.tables.Photo
import com.dotsphoto.utils.DateUtils
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.api.ExposedBlob

class PhotoRepository : LongIdDaoRepository<Photo.Table, PhotoDto, CreatePhotoDto, UpdatePhotoDto>() {
    override fun create(cdto: CreatePhotoDto): PhotoDto = insertAndGetDto {
        it[content] = ExposedBlob(cdto.content)
        it[fileName] = cdto.fileName
        it[album] = cdto.albumId
        it[createdAt] = DateUtils.now()
        it[lastUpdatedAt] = DateUtils.now()
    }

    override fun update(id: Long, udto: UpdatePhotoDto): PhotoDto? = updateById(id) {
        it[status] = status
    }

    override fun mapper(resultRow: ResultRow): PhotoDto = PhotoDto(
        resultRow[Photo.id].value,
        resultRow[Photo.content].bytes,
        resultRow[Photo.fileName],
        resultRow[Photo.createdAt],
        resultRow[Photo.lastUpdatedAt],
        resultRow[Photo.metadata]?.value,
        resultRow[Photo.status],
        resultRow[Photo.album].value,
    )

    fun findByAlbumId(albumId: Long) : List<PhotoDto> {
        return findAll { Photo.album eq albumId }
    }

    override fun getTable(): Photo.Table = Photo.Table
}
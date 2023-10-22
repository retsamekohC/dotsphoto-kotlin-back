package com.dotsphoto.orm.services.repositories

import com.dotsphoto.orm.dto.CreatePhotoMetadataDto
import com.dotsphoto.orm.dto.PhotoMetadataDto
import com.dotsphoto.orm.tables.PhotoMetadata
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert

class PhotoMetadataRepository : NonUpdatableLongIdRepository<PhotoMetadata.Table, PhotoMetadataDto, CreatePhotoMetadataDto>() {
    override fun create(cdto: CreatePhotoMetadataDto): PhotoMetadataDto = insertAndGetDto {
        it[kilobyteSize] = cdto.kilobyteSize
    }

    override fun mapper(resultRow: ResultRow): PhotoMetadataDto = PhotoMetadataDto(
        resultRow[PhotoMetadata.id].value,
        resultRow[PhotoMetadata.widthInPixels],
        resultRow[PhotoMetadata.heightInPixels],
        resultRow[PhotoMetadata.cameraMegapixels],
        resultRow[PhotoMetadata.kilobyteSize],
        resultRow[PhotoMetadata.geolocation],
        resultRow[PhotoMetadata.shotAt]
    )

    override fun getTable(): PhotoMetadata.Table = PhotoMetadata.Table
}
package com.dotsphoto.orm.services

import com.dotsphoto.orm.dto.CreatePhotoMetadataDto
import com.dotsphoto.orm.dto.PhotoMetadataDto
import com.dotsphoto.orm.services.repositories.PhotoMetadataRepository
import com.dotsphoto.orm.tables.PhotoMetadata

class PhotoMetadataService(repository: PhotoMetadataRepository) : LongIdService<PhotoMetadata.Table, PhotoMetadataDto, CreatePhotoMetadataDto>(repository) {
    fun getEmptyMetadata():PhotoMetadataDto {
        return repository.create(CreatePhotoMetadataDto(0))
    }
}
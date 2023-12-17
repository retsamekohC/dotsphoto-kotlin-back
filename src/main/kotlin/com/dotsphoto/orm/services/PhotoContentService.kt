package com.dotsphoto.orm.services

import com.dotsphoto.orm.dto.CreatePhotoContentDto
import com.dotsphoto.orm.dto.PhotoContentDto
import com.dotsphoto.orm.services.repositories.PhotoContentRepository
import com.dotsphoto.orm.tables.PhotoContent

class PhotoContentService(repository: PhotoContentRepository) : LongIdService<PhotoContent.Table, PhotoContentDto, CreatePhotoContentDto>(repository) {

}
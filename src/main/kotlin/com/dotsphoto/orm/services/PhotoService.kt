package com.dotsphoto.orm.services

import com.dotsphoto.orm.dto.CreatePhotoContentDto
import com.dotsphoto.orm.dto.CreatePhotoDto
import com.dotsphoto.orm.dto.PhotoDto
import com.dotsphoto.orm.dto.UpdatePhotoDto
import com.dotsphoto.orm.enums.Statuses
import com.dotsphoto.orm.services.repositories.PhotoRepository
import com.dotsphoto.orm.tables.Photo
import org.koin.java.KoinJavaComponent.inject
import java.util.*

class PhotoService(repository: PhotoRepository) : LongIdDaoService<Photo.Table, PhotoDto, CreatePhotoDto, UpdatePhotoDto>(repository) {

    private val ownershipService by inject<OwnershipService>(OwnershipService::class.java)

    private val userService by inject<UserService>(UserService::class.java)

    private val photoContentService by inject<PhotoContentService>(PhotoContentService::class.java)

    fun getByIdAndUser(photoId: Long, userId: Long): PhotoDto? {
        val photo = findById(photoId) ?: return null
        val isAccessible = ownershipService.checkRights(photo.albumId, userId)
        return if (isAccessible && photo.status == Statuses.ACTIVE) {
            photo
        } else {
            null
        }
    }

    fun getFromAlbumByUser(albumId: Long, userId: Long) : List<PhotoDto> {
        return if (ownershipService.checkRights(albumId, userId)) {
            (repository as PhotoRepository).findByAlbumId(albumId)
        } else {
            emptyList()
        }
    }

    fun getFromUserRoot(userId: Long) : List<PhotoDto> {
        val userRootAlbumId = userService.findById(userId)?.rootAlbumId ?: return emptyList()
        return (repository as PhotoRepository).findByAlbumId(userRootAlbumId)
    }

    fun postPhoto(bytes: ByteArray, photoName: String?, userId: Long, albumId: Long?): PhotoDto {
        var actualAlbumId = albumId
        if (actualAlbumId == null) {
            val user = userService.findById(userId) ?: throw IllegalArgumentException("user does not exists, user=${userId}")
            actualAlbumId = user.rootAlbumId
        }
        val photoContent = photoContentService.create(CreatePhotoContentDto(bytes))
        return repository.create(
            CreatePhotoDto(
                photoContent.id,
                photoName ?: UUID.randomUUID().toString(),
                actualAlbumId
            )
        )
    }

    fun postPhoto(photoContentId: Long, photoName: String?, userId: Long, albumId: Long?): PhotoDto {
        var actualAlbumId = albumId
        if (actualAlbumId == null) {
            val user = userService.findById(userId) ?: throw IllegalArgumentException("user does not exists, user=${userId}")
            actualAlbumId = user.rootAlbumId
        }
        return repository.create(
            CreatePhotoDto(
                photoContentId,
                photoName ?: UUID.randomUUID().toString(),
                actualAlbumId
            )
        )
    }

    fun copyPhoto (photoId: Long, albumId: Long, userId: Long): PhotoDto? {
        val photo = repository.findById(photoId)
        return if (photo != null) {
            postPhoto(photoContentId = photo.photoContentId, photoName = photo.fileName, userId = userId, albumId = albumId)
        } else {
            null
        }
    }

    fun movePhoto(photoId: Long, albumId: Long, userId: Long): PhotoDto? {
        val photo = repository.findById(photoId)
        return if (photo != null) {
            repository.update(photo.id, UpdatePhotoDto(Statuses.DELETED))
            postPhoto(photoContentId = photo.photoContentId, photoName = photo.fileName, userId = userId, albumId = albumId)
        } else {
            null
        }
    }

    fun removePhoto(photoId: Long, albumId: Long): Boolean {
        val photo = findById(photoId) ?: return false
        if (photo.albumId == albumId) {
            repository.update(photoId, UpdatePhotoDto(Statuses.DELETED))
            return true;
        } else {
            return false;
        }
    }
}
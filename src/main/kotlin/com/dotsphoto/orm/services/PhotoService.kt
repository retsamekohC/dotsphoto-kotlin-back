package com.dotsphoto.orm.services

import com.dotsphoto.orm.dto.CreatePhotoDto
import com.dotsphoto.orm.dto.PhotoDto
import com.dotsphoto.orm.dto.UpdatePhotoDto
import com.dotsphoto.orm.services.repositories.PhotoRepository
import com.dotsphoto.orm.tables.Photo
import io.ktor.http.content.*
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.emptySized
import org.koin.java.KoinJavaComponent.inject
import java.util.*

class PhotoService(repository: PhotoRepository) : LongIdDaoService<Photo.Table, PhotoDto, CreatePhotoDto, UpdatePhotoDto>(repository) {

    private val ownershipService by inject<OwnershipService>(OwnershipService::class.java)

    private val userService by inject<UserService>(UserService::class.java)

    fun savePhotoToUserRoot(bytes: ByteArray, photoName: String?, userId: Long): PhotoDto {
        val user = userService.findById(userId) ?: throw IllegalArgumentException("user does not exists, user=${userId}")
        val albumId = user.rootAlbumId
        return repository.create(
            CreatePhotoDto(
                bytes,
                photoName ?: UUID.randomUUID().toString(),
                albumId
            )
        )
    }

    fun getByIdAndUser(photoId: Long, userId: Long): PhotoDto? {
        val photo = findById(photoId) ?: return null
        val isAccessible = ownershipService.checkRights(photo.albumId, userId)
        return if (isAccessible) {
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
}
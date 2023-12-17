package com.dotsphoto.orm.services

import com.dotsphoto.orm.dto.AlbumDto
import com.dotsphoto.orm.dto.CreateAlbumDto
import com.dotsphoto.orm.dto.UpdateAlbumDto
import com.dotsphoto.orm.enums.Statuses
import com.dotsphoto.orm.services.repositories.AlbumRepository
import com.dotsphoto.orm.tables.Album
import org.koin.java.KoinJavaComponent.inject

class AlbumService(repository: AlbumRepository) :
    LongIdDaoService<Album.Table, AlbumDto, CreateAlbumDto, UpdateAlbumDto>(repository) {

    private val userService by inject<UserService>(UserService::class.java)
    private val ownershipService by inject<OwnershipService>(OwnershipService::class.java)

    fun createAlbum(albumName: String): AlbumDto {
        val album = repository.create(CreateAlbumDto(albumName))
        return album
    }

    fun createAlbumWithOwnership(albumName: String, userId: Long): AlbumDto {
        val album = repository.create(CreateAlbumDto(albumName))
        ownershipService.createOwnershipOwner(userId, album.id)
        return album
    }

    fun findAllByUser(userId: Long) = if (repository is AlbumRepository) {
            repository.findAllByUser(userId)
        } else {
            throw IllegalStateException("impossible album service state")
        }

    fun findByIdForUser(albumId: Long, userId: Long) = if (repository is AlbumRepository) {
        repository.findByIdForUser(albumId, userId)
    } else {
        throw IllegalStateException("impossible album service state")
    }

    fun getRootByUser(userId: Long): AlbumDto? {
        val userRootAlbumId = userService.findById(userId)?.rootAlbumId ?: return null
        return findById(userRootAlbumId)
    }

    fun getOwnedByUser(userId: Long):List<AlbumDto> {
        return (repository as AlbumRepository).findOwnedByUser(userId)
    }

    fun removeAlbum(albumId: Long):Boolean {
        val updateDto = UpdateAlbumDto(
            status = Statuses.DELETED
        )
        return repository.update(albumId, updateDto) != null
    }
}
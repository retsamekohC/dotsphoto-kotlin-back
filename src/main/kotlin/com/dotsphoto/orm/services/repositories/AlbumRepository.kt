package com.dotsphoto.orm.services.repositories

import com.dotsphoto.orm.dto.AlbumDto
import com.dotsphoto.orm.dto.CreateAlbumDto
import com.dotsphoto.orm.dto.UpdateAlbumDto
import com.dotsphoto.orm.enums.OwnershipLevel
import com.dotsphoto.orm.tables.Album
import com.dotsphoto.orm.tables.Ownership
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class AlbumRepository : LongIdDaoRepository<Album.Table, AlbumDto, CreateAlbumDto, UpdateAlbumDto>() {
    override fun create(cdto: CreateAlbumDto): AlbumDto = insertAndGetDto {
        it[albumName] = cdto.albumName
    }

    override fun update(id: Long, udto: UpdateAlbumDto): AlbumDto? = updateById(id) {
        it[status] = udto.status
    }

    override fun mapper(resultRow: ResultRow): AlbumDto = AlbumDto(
        resultRow[Album.id].value,
        resultRow[Album.albumName],
        resultRow[Album.createdAt],
        resultRow[Album.lastUpdatedAt],
        resultRow[Album.status]
    )

    fun findAllByUser(userId: Long): List<AlbumDto>  = transaction {
        Album.join(Ownership, JoinType.INNER, onColumn = Album.id, otherColumn = Ownership.album, additionalConstraint = { Ownership.user eq userId})
            .slice(Album.fields)
            .selectAll()
            .mapLazy { mapper(it) }
            .toList()
    }

    fun findByIdForUser(albumId: Long, userId: Long) : AlbumDto? {
        return findAllByUser(userId).singleOrNull { it.id == albumId }
    }

    fun findOwnedByUser(userId:Long) : List<AlbumDto> = transaction {
        Album.join(Ownership, JoinType.INNER,
            onColumn = Album.id, otherColumn = Ownership.album,
            additionalConstraint = { Ownership.user eq userId and (Ownership.level eq OwnershipLevel.OWNER)})
            .slice(Album.fields)
            .selectAll()
            .map { mapper(it) }
    }

    override fun getTable(): Album.Table = Album.Table
}
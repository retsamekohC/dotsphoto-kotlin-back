package com.dotsphoto.orm.services.repositories

import com.dotsphoto.orm.dto.*
import com.dotsphoto.orm.services.UserService
import com.dotsphoto.orm.tables.Album
import com.dotsphoto.orm.tables.Ownership
import com.dotsphoto.orm.tables.User
import org.jetbrains.exposed.sql.*

class OwnershipRepository : LongIdDaoRepository<Ownership.Table, OwnershipDto, CreateOwnerhshipDto, UpdateOwnershipDto>() {
    override fun create(cdto: CreateOwnerhshipDto): OwnershipDto = insertAndGetDto {
        it[album] = cdto.albumId
        it[user] = cdto.userId
        it[level] = cdto.level
    }

    override fun update(id: Long, udto: UpdateOwnershipDto): OwnershipDto? = updateById(id) {
        it[level] = udto.level
    }

    override fun mapper(resultRow: ResultRow): OwnershipDto = OwnershipDto(
        resultRow[Ownership.id].value,
        resultRow[Ownership.album].value,
        resultRow[Ownership.user].value,
        resultRow[Ownership.level]
    )

    override fun getTable(): Ownership.Table = Ownership.Table
}
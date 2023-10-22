package com.dotsphoto.orm.services.repositories

import com.dotsphoto.orm.dto.CreateUserDto
import com.dotsphoto.orm.dto.UpdateUserDto
import com.dotsphoto.orm.dto.UserDto
import com.dotsphoto.orm.tables.User
import org.jetbrains.exposed.sql.ResultRow

class UserRepository : LongIdDaoRepository<User.Table, UserDto, CreateUserDto, UpdateUserDto>() {
    override fun create(cdto: CreateUserDto): UserDto = insertAndGetDto {
        it[nickname] = cdto.nickname
        it[email] = cdto.email
        it[fullName] = cdto.fullName
        it[rootAlbum] = cdto.rootAlbumId
        it[subscription] = cdto.subscriptionId
    }

    override fun update(id: Long, udto: UpdateUserDto): UserDto? = updateById(id) {
        it[status] = udto.status
    }

    override fun mapper(resultRow: ResultRow): UserDto = UserDto(
        resultRow[User.id].value,
        resultRow[User.nickname],
        resultRow[User.email],
        resultRow[User.fullName],
        resultRow[User.rootAlbum].value,
        resultRow[User.subscription].value,
        resultRow[User.status],
    )

    override fun getTable(): User.Table = User.Table
}
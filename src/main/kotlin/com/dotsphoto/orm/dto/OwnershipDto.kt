package com.dotsphoto.orm.dto

import com.dotsphoto.orm.enums.OwnershipLevel
import com.dotsphoto.orm.tables.Ownership
import com.dotsphoto.orm.util.CreateLongDto
import com.dotsphoto.orm.util.LongIdTableDto
import com.dotsphoto.orm.util.UpdateLongDto

data class OwnershipDto(
    override val id: Long,
    val albumId: Long,
    val userId: Long,
    val level: OwnershipLevel
) : LongIdTableDto<Ownership.Table>

data class CreateOwnerhshipDto(
    val albumId: Long,
    val userId: Long,
    val level: OwnershipLevel
) : CreateLongDto<Ownership.Table>

data class UpdateOwnershipDto(
    val level: OwnershipLevel
) : UpdateLongDto<Ownership.Table>
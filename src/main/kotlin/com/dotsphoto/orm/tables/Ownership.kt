package com.dotsphoto.orm.tables

import com.dotsphoto.orm.enums.OwnershipLevel
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

class Ownership(database: Database) {
    companion object Table : LongIdTable(name = "ownership") {
        val user = reference("user_id", User).index("ownerhship_user_fk_idx")
        val album = reference("album_id", Album).index("ownerhship_album_fk_idx")
        val level = enumerationByName(
            "level",
            OwnershipLevel.entries.maxBy { it.toString().length }.toString().length,
            OwnershipLevel::class
        ).default(OwnershipLevel.OWNER)
    }

    init {
        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(Table)
        }
    }
}
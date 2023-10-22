package com.dotsphoto.orm.tables

import com.dotsphoto.orm.enums.Statuses
import com.dotsphoto.utils.DateUtils
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject

class Album(database: Database){
    companion object Table : LongIdTable(name = "album") {
        val albumName = varchar("album_name", 256)
        val createdAt = datetime("created_at").default(DateUtils.now())
        val lastUpdatedAt = datetime("last_updated_at").default(DateUtils.now())
        val status = enumerationByName(
            "status",
            Statuses.entries.maxBy { it.toString().length }.toString().length,
            Statuses::class
        ).default(Statuses.ACTIVE)
    }

    init {
        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(Table)
        }
    }
}


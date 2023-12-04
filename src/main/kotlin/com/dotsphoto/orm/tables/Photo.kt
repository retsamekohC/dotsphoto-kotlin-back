package com.dotsphoto.orm.tables

import com.dotsphoto.orm.enums.Statuses
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

class Photo(database: Database) {
    companion object Table : LongIdTable(name = "photo") {
        val content = blob("content")
        val compressedContent = blob("compressed_content")
        val fileName = varchar("filename", 256)
        val createdAt = datetime("created_at")
        val lastUpdatedAt = datetime("last_updated_at")
        val metadata =
            reference("metadata_id", PhotoMetadata).nullable().index("photo_metadata_fk_idx")
        val status = enumerationByName(
            "status",
            Statuses.entries.maxBy { it.toString().length }.toString().length,
            Statuses::class
        ).default(Statuses.ACTIVE)
        val album = reference("album_id", Album.Table).index("photo_album_fk_idx")
    }

    init {
        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(Table)
        }
    }
}
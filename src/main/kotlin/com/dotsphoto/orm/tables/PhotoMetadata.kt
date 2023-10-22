package com.dotsphoto.orm.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

class PhotoMetadata(database: Database) {
    companion object Table : LongIdTable(name = "photo_metadata") {
        val widthInPixels = integer("width_in_pixels").nullable()
        val heightInPixels = integer("height_in_pixels").nullable()
        val cameraMegapixels = float("camera_megapixels").nullable()
        val kilobyteSize = integer("kilobyte_size").nullable()
        val geolocation = varchar("geolocation", 4000).nullable()
        val shotAt = datetime("shot_at").nullable()
    }

    init {
        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(Table)
        }
    }
}
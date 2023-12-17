package com.dotsphoto.orm.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class PhotoContent(database: Database) {
    companion object Table : LongIdTable(name = "photo_content") {
        val content = blob("content")
        val compressedContent = blob("compressed_content")
    }

    init {
        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(Table)
        }
    }
}
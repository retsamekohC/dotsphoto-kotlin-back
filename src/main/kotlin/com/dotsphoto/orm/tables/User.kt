package com.dotsphoto.orm.tables

import com.dotsphoto.orm.enums.Statuses
import com.dotsphoto.orm.tables.Album.Table.default
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

class User(database: Database) {
    companion object Table : LongIdTable(name = "user") {
        val nickname = varchar("nickname", 256).nullable()
        val email = varchar("email", 256).uniqueIndex("user_email_uq_idx")
        val fullName = varchar("full_name", 256).nullable()
        val rootAlbum = reference("root_album_id", Album.Table).index("user_root_album_fk_idx")
        val subscription = reference("subscription_id", Subscription.Table).index("user_subscription_fk_idx")
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
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
        val nickname = varchar("nickname", 256)
        val rootAlbum = reference("root_album_id", Album.Table).index("user_root_album_fk_idx")
        val subscription = reference("subscription_id", Subscription.Table).index("user_subscription_fk_idx")
        val status = enumerationByName(
            "status",
            Statuses.entries.maxBy { it.toString().length }.toString().length,
            Statuses::class
        ).default(Statuses.ACTIVE)
        val userCreds = varchar("user_creds", 256).index("user_creds_search_idx")
    }

    init {
        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(Table)
        }
    }
}
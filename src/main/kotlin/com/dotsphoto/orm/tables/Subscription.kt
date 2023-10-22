package com.dotsphoto.orm.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

class Subscription(database: Database) {
    companion object Table : LongIdTable(name = "subscription") {
        val plan = reference("plan_id", SubscriptionPlan.Table).index("subscription_plan_fk_idx")
        val dateFrom = datetime("date_from")
        val dateTo = datetime("date_to")

        init {
            index(customIndexName = "date_between_idx", columns = arrayOf(dateFrom, dateTo))
        }
    }

    init {
        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(Table)
        }
    }
}
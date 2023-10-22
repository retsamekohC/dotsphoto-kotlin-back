package com.dotsphoto.orm.tables

import com.dotsphoto.orm.enums.Statuses
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

class SubscriptionPlan(database: Database) {
    companion object Table : LongIdTable(name = "subscription_plan") {
        val planName = varchar("plan_name", 256).uniqueIndex()
        val availableSpaceGb = integer("available_space_gb")
        val periodMonths = integer("period_months")
        val priceCop = integer("price_cop")
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
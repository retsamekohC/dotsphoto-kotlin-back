package com.dotsphoto.plugins

import com.dotsphoto.orm.tables.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabases(config: ApplicationConfig) {
    val database = Database.connect(
        url = config.property("storage.jdbcUrl").getString(),
        user = config.property("storage.user").getString(),
        driver = config.property("storage.driverClassName").getString(),
        password = config.property("storage.password").getString()
    )

    Album(database)
    Ownership(database)
    Photo(database)
    PhotoMetadata(database)
    Subscription(database)
    SubscriptionPlan(database)
    User(database)
}

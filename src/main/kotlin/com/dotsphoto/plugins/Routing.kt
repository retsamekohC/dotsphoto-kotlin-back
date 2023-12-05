package com.dotsphoto.plugins

import com.dotsphoto.api.controllers.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        authRoutes()
        userRoutes()
        photoRoutes()
        albumRoutes()
        ownerShipRoutes()
    }
}

package com.dotsphoto.api.controllers

import com.dotsphoto.orm.services.UserService
import com.dotsphoto.plugins.GoogleSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.koin.java.KoinJavaComponent.inject

const val baseUrl = "/user"

fun Route.userRoutes() {
    val userService by inject<UserService>(UserService::class.java)

    get("$baseUrl/get/{id}") {
        val id = call.parameters["id"]?.toLong()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest)
        } else {
            val user = userService.findById(id)
            if (user == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(user)
            }
        }
    }
}
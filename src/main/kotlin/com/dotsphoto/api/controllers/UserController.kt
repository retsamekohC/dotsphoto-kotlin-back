package com.dotsphoto.api.controllers

import com.dotsphoto.orm.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent.inject

const val baseUrl = "/user"

fun Route.userRoutes() {
    val userService by inject<UserService>(UserService::class.java)

    route(baseUrl) {
        authAndCall(::get, "/{id}") {
            val id = call.getParameter("id") { it.toLong() }
            val user = userService.findById(id)
            if (user == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(user)
            }
        }
    }

}
package com.dotsphoto.api.controllers

import com.dotsphoto.api.controllers.dto.UserApiDto
import com.dotsphoto.orm.services.UserService
import com.dotsphoto.plugins.SecurityConsts
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent.inject

const val baseUrl = "/user"

fun Route.userRoutes() {
    val userService by inject<UserService>(UserService::class.java)

    authenticate(SecurityConsts.USER_SESSION) {
        route(baseUrl) {
            authAndCall(::get, "/{id}") {
                val id = call.getParameter("id") { it.toLong() }
                val user = userService.findById(id)
                if (user == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond<UserApiDto>(UserApiDto.from(user))
                }
            }

            authAndCall(::get, "/me") {
                val user = userService.findById(authSession().id)
                if (user == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond<UserApiDto>(UserApiDto.from(user))
                }
            }

            authAndCall(::get, "") {
                call.respond<List<UserApiDto>>(userService.getAllUsers().map { UserApiDto.from(it) })
            }
        }
    }

}
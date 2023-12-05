package com.dotsphoto.api.controllers

import com.dotsphoto.api.controllers.dto.PhotoApiDto
import com.dotsphoto.api.controllers.dto.request.bodies.PhotoPostRequest
import com.dotsphoto.orm.services.PhotoService
import com.dotsphoto.plugins.SecurityConsts.USER_SESSION
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent.inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun Route.photoRoutes() {
    val baseUrl = "/photo"

    val photoService by inject<PhotoService>(PhotoService::class.java)

    authenticate(USER_SESSION) {
        route(baseUrl) {
            authAndCall(::post, "") {
                val userId = authSession().id
                val photoPostRequest = call.receive<PhotoPostRequest>()
                val bytes = Base64.decode(photoPostRequest.b64)
                photoService.savePhotoToUserRoot(bytes, photoPostRequest.photoName, userId)
                call.respond(HttpStatusCode.OK)
            }

            authAndCall(::get, "/{id}") {
                val id = call.getParameter("id") { it.toLong() }
                val compressed = call.getParameter("compressed") { it.toBoolean() }
                val userId = authSession().id
                val photo = photoService.getByIdAndUser(id, userId)
                if (photo == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond<PhotoApiDto>(PhotoApiDto.from(photo, compressed))
                }
            }
        }
    }
}
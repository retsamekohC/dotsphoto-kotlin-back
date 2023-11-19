package com.dotsphoto.api.controllers

import com.dotsphoto.orm.services.PhotoService
import com.dotsphoto.orm.services.UserService
import com.dotsphoto.plugins.UserSession
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun Route.photoRoutes() {
    val baseUrl = "/photo"

    val photoService by inject<PhotoService>(PhotoService::class.java)
    val userService by inject<UserService>(PhotoService::class.java)

    post(baseUrl) {
        val userSession = call.sessions.get<UserSession>()
        if (userSession == null) {
            call.respond(HttpStatusCode.Unauthorized)
        } else {
            val parts = call.receiveMultipart()
            var file:PartData.FileItem? = null
            parts.forEachPart {part ->
                when (part) {
                    is PartData.FileItem -> {
                        file = part
                    }
                    else -> {}
                }
            }
            val bytes = file!!.streamProvider().readBytes()
            photoService.savePhotoToUserRoot(bytes, file!!.originalFileName, userSession.userId)
            call.respond(HttpStatusCode.OK)
        }
    }

    get("$baseUrl/{id}") {
        val id = call.parameters["id"]?.toLong()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest)
        } else {
            val userSession = call.sessions.get<UserSession>()
            if (userSession == null) {
                call.respond(HttpStatusCode.Unauthorized)
            } else {
                val userId = userSession.userId
                val photo = photoService.getByIdAndUser(id, userId)
                if (photo == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond(photo)
                }
            }
        }
    }
}
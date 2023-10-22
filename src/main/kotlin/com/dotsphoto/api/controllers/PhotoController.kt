package com.dotsphoto.api.controllers

import com.dotsphoto.orm.services.PhotoService
import com.dotsphoto.orm.services.UserService
import com.dotsphoto.plugins.GoogleSession
import com.dotsphoto.plugins.UserSession
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.koin.java.KoinJavaComponent.inject

fun Route.photoRoutes() {
    val baseUrl = "/photo"

    val photoService by inject<PhotoService>(PhotoService::class.java)
    val userService by inject<UserService>(PhotoService::class.java)

    post(baseUrl) {
        val parts = call.receiveMultipart().readAllParts()
        val userSession = call.sessions.get<UserSession>()
        if (userSession == null) {
            call.respond(HttpStatusCode.Unauthorized)
        } else {
            val photoFile = parts.find { it is PartData.FileItem } as PartData.FileItem
            val albumId = parts.find { it is PartData.FormItem } as PartData.FormItem
            photoService.savePhotoToUserRoot(photoFile, userSession.userId)
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
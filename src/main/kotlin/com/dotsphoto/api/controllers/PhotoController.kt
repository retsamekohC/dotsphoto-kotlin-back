package com.dotsphoto.api.controllers

import com.dotsphoto.orm.services.PhotoService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent.inject

fun Route.photoRoutes() {
    val baseUrl = "/photo"

    val photoService by inject<PhotoService>(PhotoService::class.java)

    route(baseUrl) {
        authAndCall(::post, "") {
            val userId = authSession().id
            val parts = call.receiveMultipart()
            var file: PartData.FileItem? = null
            parts.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        file = part
                    }

                    else -> {}
                }
            }
            val bytes = file!!.streamProvider().readBytes()
            photoService.savePhotoToUserRoot(bytes, file!!.originalFileName, userId)
            call.respond(HttpStatusCode.OK)

        }

        authAndCall(::get, "/{id}") {
            val id = call.getParameter("id") { it.toLong() }
            val userId = authSession().id
            val photo = photoService.getByIdAndUser(id, userId)
            if (photo == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(photo)
            }

        }
    }
}
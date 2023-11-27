package com.dotsphoto.api.controllers

import com.dotsphoto.orm.services.AlbumService
import com.dotsphoto.orm.services.PhotoService
import com.dotsphoto.plugins.SecurityConsts.USER_SESSION
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.mapLazy
import org.koin.java.KoinJavaComponent.inject

fun Route.albumRoutes() {
    val baseUrl = "/album"
    val albumService by inject<AlbumService>(AlbumService::class.java)
    val photoService by inject<PhotoService>(PhotoService::class.java)

    authenticate(USER_SESSION) {
        route(baseUrl) {
            authAndCall(::get, "") {
                val userId = authSession().id
                val albums = albumService.findAllByUser(userId)
                call.respond(albums)
            }

            authAndCall(::get, "/root") {
                val userId = authSession().id
                val rootAlbum = albumService.getRootByUser(userId)
                if (rootAlbum == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond(rootAlbum)
                }
            }

            authAndCall(::get, "/get/{id}") {
                val albumId = call.parameters["id"]?.toLong()
                if (albumId == null) {
                    call.respond(HttpStatusCode.BadRequest)
                } else {
                    val userId = authSession().id
                    val album = albumService.findByIdForUser(albumId, userId)
                    if (album == null) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        call.respond(album)
                    }
                }
            }

            authAndCall(::get ,"/{id}/photos") {
                val albumId = call.parameters["id"]?.toLong()
                val userId = authSession().id
                if (albumId == null) {
                    call.respond(HttpStatusCode.BadRequest)
                } else {
                    call.respond(photoService.getFromAlbumByUser(albumId, userId).toList().map { it.id })
                }
            }
        }
    }
}
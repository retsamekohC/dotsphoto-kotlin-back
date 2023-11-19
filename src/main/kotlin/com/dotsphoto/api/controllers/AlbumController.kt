package com.dotsphoto.api.controllers

import com.dotsphoto.orm.services.AlbumService
import com.dotsphoto.orm.services.PhotoService
import com.dotsphoto.plugins.GoogleSession
import com.dotsphoto.plugins.UserSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.jetbrains.exposed.sql.mapLazy
import org.koin.java.KoinJavaComponent.inject

fun Route.albumRoutes() {
    val baseUrl = "/album"
    val albumService by inject<AlbumService>(AlbumService::class.java)
    val photoService by inject<PhotoService>(PhotoService::class.java)

    get("$baseUrl/get") {
        val userSession = call.sessions.get<UserSession>()
        if (userSession == null) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            val albums = albumService.findAllByUser(userSession.userId)
            call.respond(albums)
        }
    }

    get("$baseUrl/root") {
        val userSession = call.sessions.get<UserSession>()
        val googleSession = call.sessions.get<GoogleSession>()
        if (userSession == null) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            val rootAlbum = albumService.getRootByUser(userSession.userId)
            if (rootAlbum == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(rootAlbum)
            }
        }
    }

    get("$baseUrl/get/{id}") {
        val albumId = call.parameters["id"]?.toLong()
        val userSession = call.sessions.get<UserSession>()
        if (userSession == null || albumId == null) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            val album = albumService.findByIdForUser(albumId, userSession.userId)
            if (album == null) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(this)
            }
        }
    }

    get("$baseUrl/{id}/photos") {
        val albumId = call.parameters["id"]?.toLong()
        val userSession = call.sessions.get<UserSession>()
        if (albumId == null) {
            call.respond(HttpStatusCode.BadRequest)
        } else if (userSession == null) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            call.respond(photoService.getFromAlbumByUser(albumId, userSession.userId).mapLazy { it.id })
        }
    }
}
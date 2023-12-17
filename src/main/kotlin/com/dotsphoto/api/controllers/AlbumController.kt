package com.dotsphoto.api.controllers

import com.dotsphoto.api.controllers.dto.AlbumApiDto
import com.dotsphoto.api.controllers.dto.request.bodies.NewAlbumRequest
import com.dotsphoto.api.controllers.dto.request.bodies.RemoveAlbumRequest
import com.dotsphoto.api.controllers.dto.request.bodies.ShareRequest
import com.dotsphoto.orm.services.AlbumService
import com.dotsphoto.orm.services.OwnershipService
import com.dotsphoto.orm.services.PhotoService
import com.dotsphoto.plugins.SecurityConsts.USER_SESSION
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent.inject

fun Route.albumRoutes() {
    val baseUrl = "/album"
    val albumService by inject<AlbumService>(AlbumService::class.java)
    val photoService by inject<PhotoService>(PhotoService::class.java)
    val ownershipService by inject<OwnershipService>(OwnershipService::class.java)

    authenticate(USER_SESSION) {
        route(baseUrl) {
            authAndCall(::get, "") {
                val userId = authSession().id
                val albums = albumService.findAllByUser(userId)
                call.respond<List<AlbumApiDto>>(albums.map { AlbumApiDto.from(it) })
            }

            authAndCall(::get, "/root") {
                val userId = authSession().id
                val rootAlbum = albumService.getRootByUser(userId)
                if (rootAlbum == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond<AlbumApiDto>(AlbumApiDto.from(rootAlbum))
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
                        call.respond<AlbumApiDto>(AlbumApiDto.from(album))
                    }
                }
            }

            authAndCall(::get, "/{id}/photos") {
                val albumId = call.parameters["id"]?.toLong()
                val userId = authSession().id
                if (albumId == null) {
                    call.respond(HttpStatusCode.BadRequest)
                } else {
                    call.respond<List<Long>>(photoService.getFromAlbumByUser(albumId, userId).toList().map { it.id })
                }
            }

            authAndCall(::post, "/share") {
                val shareRequest = call.receive<ShareRequest>()
                val callUserId = authSession().id
                if (ownershipService.checkRightsOwner(shareRequest.albumId, callUserId)) {
                    if (ownershipService.checkRights(shareRequest.albumId, shareRequest.userId)) {
                        call.respond(HttpStatusCode.OK)
                    } else {
                        ownershipService.createOwnershipView(shareRequest.userId, shareRequest.albumId)
                        call.respond(HttpStatusCode.OK)
                    }
                } else {
                    call.respond(HttpStatusCode.Forbidden)
                }
            }

            authAndCall(::get, "/my") {
                val userId = authSession().id
                call.respond<List<AlbumApiDto>>(albumService.getOwnedByUser(userId).map { AlbumApiDto.from(it) })
            }

            authAndCall(::post, "/new") {
                val userId = authSession().id
                if (!authSession().authed) {
                    call.respond(HttpStatusCode.Unauthorized)
                } else {
                    val newAlbumRequest = call.receive<NewAlbumRequest>()
                    call.respond<AlbumApiDto>(
                        AlbumApiDto.from(
                            albumService.createAlbumWithOwnership(
                                albumName = newAlbumRequest.albumName,
                                userId = userId
                            )
                        )
                    )
                }
            }

            authAndCall(::post, "/remove") {
                val userId = authSession().id
                if (!authSession().authed) {
                    call.respond(HttpStatusCode.Unauthorized)
                } else {
                    val removeAlbumRequest = call.receive<RemoveAlbumRequest>()
                    if (ownershipService.checkRightsOwner(albumId = removeAlbumRequest.albumId, userId = userId)) {
                        call.respond(
                            if (albumService.removeAlbum(albumId = removeAlbumRequest.albumId))
                                HttpStatusCode.OK
                            else
                                HttpStatusCode.InternalServerError
                        )
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                }
            }
        }
    }
}
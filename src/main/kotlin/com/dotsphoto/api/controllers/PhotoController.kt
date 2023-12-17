package com.dotsphoto.api.controllers

import com.dotsphoto.api.controllers.dto.PhotoApiDto
import com.dotsphoto.api.controllers.dto.request.bodies.CopyPhotoRequest
import com.dotsphoto.api.controllers.dto.request.bodies.MovePhotoRequest
import com.dotsphoto.api.controllers.dto.request.bodies.PhotoPostRequest
import com.dotsphoto.api.controllers.dto.request.bodies.RemovePhotoRequest
import com.dotsphoto.orm.services.OwnershipService
import com.dotsphoto.orm.services.PhotoContentService
import com.dotsphoto.orm.services.PhotoService
import com.dotsphoto.orm.services.UserService
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

    val ownershipService by inject<OwnershipService>(OwnershipService::class.java)

    val userService by inject<UserService>(UserService::class.java)

    val photoContentService by inject<PhotoContentService>(PhotoContentService::class.java)

    authenticate(USER_SESSION) {
        route(baseUrl) {

            authAndCall(::post, "") {
                val userId = authSession().id
                val photoPostRequest = call.receive<PhotoPostRequest>()
                val bytes = Base64.decode(photoPostRequest.b64)
                if (authSession().authed) {
                    val albumId = photoPostRequest.albumId ?: userService.findById(userId)!!.rootAlbumId
                    if (ownershipService.checkRightsOwner(albumId = albumId, userId = userId)) {
                        photoService.postPhoto(bytes = bytes, photoName = photoPostRequest.photoName, userId = userId, albumId = photoPostRequest.albumId)
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                } else {
                    call.respond(HttpStatusCode.Unauthorized)
                }
            }

            authAndCall(::post, "/removeFromAlbum") {
                val userId = authSession().id
                val removePhotoRequest = call.receive<RemovePhotoRequest>()
                if (ownershipService.checkRightsOwner(albumId = removePhotoRequest.albumId, userId = userId)) {
                    val result = photoService.removePhoto(photoId = removePhotoRequest.photoId, albumId = removePhotoRequest.albumId)
                    call.respond(if (result) HttpStatusCode.OK else HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.Unauthorized)
                }
            }

            authAndCall(::post, "/moveToAlbum") {
                val movePhotoRequest = call.receive<MovePhotoRequest>()
                if (ownershipService.checkRightsOwner(albumId = movePhotoRequest.albumId, userId = authSession().id)) {
                    val photoCurrentAlbumId = photoService.findById(movePhotoRequest.photoId)?.albumId
                    if (photoCurrentAlbumId != null &&
                        ownershipService.checkRightsOwner(albumId = photoCurrentAlbumId, userId = authSession().id)) {
                        val result = photoService.movePhoto(photoId = movePhotoRequest.photoId, albumId = movePhotoRequest.albumId, userId = authSession().id)

                        call.respond(if (result == null) HttpStatusCode.NoContent else HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                } else {
                    call.respond(HttpStatusCode.Unauthorized)
                }
            }

            authAndCall(::post, "/copyToAlbum") {
                val copyPhotoRequest = call.receive<CopyPhotoRequest>()
                if (ownershipService.checkRightsOwner(albumId = copyPhotoRequest.albumId, userId = authSession().id)) {
                    val photoCurrentAlbumId = photoService.findById(copyPhotoRequest.photoId)?.albumId
                    if (photoCurrentAlbumId != null &&
                        ownershipService.checkRightsOwner(albumId = photoCurrentAlbumId, userId = authSession().id)) {
                        val result = photoService.copyPhoto(photoId = copyPhotoRequest.photoId, albumId = copyPhotoRequest.albumId, userId = authSession().id)
                        call.respond(if (result == null) HttpStatusCode.NoContent else HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                } else {
                    call.respond(HttpStatusCode.Unauthorized)
                }
            }

            authAndCall(::get, "/{id}") {
                val id = call.getParameter("id") { it.toLong() }
                val compressed = call.getParameter("compressed") { it.toBoolean() }
                val userId = authSession().id
                val photo = photoService.getByIdAndUser(id, userId)
                if (photo == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    val photoContentDto = photoContentService.findById(photo.photoContentId)!!
                    call.respond<PhotoApiDto>(PhotoApiDto.from(photo, photoContentDto, compressed))
                }
            }
        }
    }
}
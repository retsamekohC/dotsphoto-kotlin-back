package com.dotsphoto.api.controllers

import com.dotsphoto.api.controllers.dto.AlbumApiDto
import com.dotsphoto.api.controllers.dto.UserApiDto
import com.dotsphoto.api.controllers.dto.request.bodies.AlbumAccessibleByUsers
import com.dotsphoto.orm.services.AlbumService
import com.dotsphoto.orm.services.OwnershipService
import com.dotsphoto.orm.services.UserService
import com.dotsphoto.plugins.SecurityConsts
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.ownerShipRoutes() {

    val BASE_URL = "ownership"

    val albumService by inject<AlbumService>()
    val ownershipService by inject<OwnershipService>()
    val userService by inject<UserService>()

    authenticate(SecurityConsts.USER_SESSION) {
        route(BASE_URL) {
            authAndCall(::get, "/accessorsToMyAlbum") {
                val userId = authSession().id
                val albumId = call.getParameter("albumId") { it.toLong() }
                val albumDto = albumService.findById(albumId)
                if (albumDto != null && ownershipService.checkRightsOwner(albumId, userId)) {
                    call.respond<AlbumAccessibleByUsers>(AlbumAccessibleByUsers(
                        AlbumApiDto.from(albumDto),
                        userService.getUsersWithAccessTo(albumDto.id).map(UserApiDto.Companion::from)
                    ))
                }
            }
        }
    }
}
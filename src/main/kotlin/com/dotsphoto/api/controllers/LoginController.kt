package com.dotsphoto.api.controllers

import com.dotsphoto.api.controllers.dto.request.bodies.RegisterRequest
import com.dotsphoto.orm.services.UserService
import com.dotsphoto.orm.util.Utils
import com.dotsphoto.plugins.SecurityConsts.AUTH_BASIC
import com.dotsphoto.plugins.SecurityConsts.USER_SESSION
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Defines the authentication routes for the application.
 * These routes handle user registration, login, and logout.
 */
@OptIn(ExperimentalEncodingApi::class)
fun Route.authRoutes() {
    val userService: UserService by inject<UserService>()
    route("/auth") {

        /**
         * POST endpoint at `/register` for user registration.
         * Receives a com.dotsphoto.api.controllers.dto.request.bodies.RegisterRequest and encodes the name and password.
         * If a user with the same hashed credentials exists, a No Content (204) response is sent.
         * Otherwise, a new user is registered and an Accepted (202) response is sent.
         */
        post("/register") {
            val registerRequest = call.receive<RegisterRequest>()
            val b64 = Base64.encode("${registerRequest.name}:${registerRequest.password}".toByteArray(Charsets.UTF_8))
            val creds = Utils.getSHA1Hash(b64)
            if (userService.findByCreds(creds) != null) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                userService.registerUser(registerRequest.name, creds)
                call.respond(HttpStatusCode.Accepted)
            }
        }

        authenticate(AUTH_BASIC) {

            authAndCall(::post, "/login") {
                val session = authSession()
                call.sessions.set(USER_SESSION, session)
                call.respond(if (session.authed) HttpStatusCode.Accepted else HttpStatusCode.Unauthorized)
            }
            /**
             * HTTP POST endpoint at `/logout` for user logout.
             *
             * This endpoint ends the user session by clearing it.
             * The server then responds with HTTP status code 200 (OK) to indicate successful termination of the session.
             */
            post("/logout") {
                call.sessions.clear(USER_SESSION)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

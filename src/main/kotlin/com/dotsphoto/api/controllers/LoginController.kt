package com.dotsphoto.api.controllers

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
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Serializable
data class RegisterRequest(val name: String, val password: String)

@OptIn(ExperimentalEncodingApi::class)
fun Route.authRoutes() {
    val userService: UserService by inject<UserService>()
    route("/auth") {
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
                call.respond(HttpStatusCode.Accepted)
            }
            post("/logout") {
                call.sessions.clear(USER_SESSION)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

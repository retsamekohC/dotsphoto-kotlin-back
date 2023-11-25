package com.dotsphoto.plugins

import com.dotsphoto.orm.services.UserService
import com.dotsphoto.orm.util.Utils
import com.dotsphoto.plugins.SecurityConsts.AUTH_BASIC
import com.dotsphoto.plugins.SecurityConsts.USER_SESSION
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.server.sessions.serialization.*
import io.ktor.utils.io.charsets.*
import kotlinx.coroutines.ThreadContextElement
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.koin.ktor.ext.inject
import java.lang.RuntimeException
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Serializable
data class UserSession(val id: Long, val authed: Boolean) : Principal

@OptIn(ExperimentalEncodingApi::class)
fun Application.configureSecurity() {
    install(Sessions) {
        cookie<UserSession>(USER_SESSION) {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 600
            serializer = KotlinxSessionSerializer(serializer(), Json)
        }
    }

    val userService: UserService by inject<UserService>()

    authentication {
        basic(AUTH_BASIC) {
            realm = "Access to '/' path"
            validate { credential ->
                val b64 = Base64.encode("${credential.name}:${credential.password}".toByteArray(Charsets.UTF_8))
                val userId = userService.findByCreds(Utils.getSHA1Hash(b64))?.id
                UserSession(userId ?: 0, userId != null)
            }
        }
    }

    authentication {
        session<UserSession>(USER_SESSION) {
            validate { session ->
                if (session.authed && userService.findById(session.id) != null) session else null
            }
            challenge {
                call.respond(UnauthorizedResponse())
            }
        }
    }
}

object SecurityConsts {
    const val AUTH_BASIC = "auth-basic"
    const val USER_SESSION = "user-session"
}


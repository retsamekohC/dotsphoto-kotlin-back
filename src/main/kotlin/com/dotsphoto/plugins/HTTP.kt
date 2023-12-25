package com.dotsphoto.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.util.*

fun Application.configureHTTP() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Head)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowNonSimpleContentTypes = true
        allowHeaders { it in setOf("authorization", "content-type", "x-auth-token", "user_session") }
        allowOrigins { it in setOf(
            "http://localhost:3000",
            "http://localhost:3000/",
            "http://localhost:8080/",
            "http://localhost:8082",
            "http://localhost:8082/",
            "https://bba8bfj47dlr7fbf7cvv.containers.yandexcloud.net/"
        ) }
    }
}

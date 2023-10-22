package com.dotsphoto

import com.dotsphoto.plugins.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

val applicationHttpClient = HttpClient(CIO) {
    install(ContentEncoding) {
        deflate(1.0F)
        gzip(0.9F)
    }
    install(ContentNegotiation) {
        json()
    }
}

fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start(wait = true)
}

fun Application.module(httpClient: HttpClient = applicationHttpClient) {
    configureSecurity(environment.config, httpClient)
    configureHTTP()
    configureSerialization()
    configureDatabases(environment.config)
    configureRouting()
    configureDI()
}

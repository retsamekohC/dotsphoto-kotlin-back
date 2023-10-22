package com.dotsphoto.plugins

import com.dotsphoto.api.controllers.albumRoutes
import com.dotsphoto.api.controllers.photoRoutes
import com.dotsphoto.api.controllers.userRoutes
import com.dotsphoto.orm.services.UserService
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.config.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.p
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.java.KoinJavaComponent.inject

data class UserSession(val userId: Long)
data class GoogleSession(val state: String, val token: String)

@Serializable
data class UserInfo(
    val id: String,
    val email: String,
    @SerialName("verified_email")val verifiedEmail: Boolean,
    val name: String,
    @SerialName("given_name") val givenName: String,
    @SerialName("family_name") val familyName: String,
    val picture: String,
    val locale: String, )

fun Application.configureSecurity(config: ApplicationConfig, httpClient: HttpClient) {
    install(Sessions) {
        cookie<GoogleSession>("google_session", SessionStorageMemory())
        cookie<UserSession>("user_session", SessionStorageMemory())
    }

    val redirects = mutableMapOf<String, String>()
    authentication {
        oauth("auth-oauth-google") {
            urlProvider = { "http://localhost:8080/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                    accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = config.property("auth.clientId").getString(),
                    clientSecret = config.property("auth.clientSecret").getString(),
                    defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile", "https://www.googleapis.com/auth/userinfo.email"),
                    extraAuthParameters = listOf("access_type" to "offline"),
                    onStateCreated = { call, state ->
                        redirects[state] = call.request.queryParameters["redirectUrl"] ?: ""
                    }
                )
            }
            client = httpClient
        }
    }
    routing {
        val userService by inject<UserService>(UserService::class.java)

        get("/") {
            call.respondHtml {
                body {
                    p {
                        a("/login?redirectUrl=album/root") { +"Login with Google" }
                    }
                }
            }
        }
        get("/fillUserInfo") {
            val googleSession: GoogleSession? = call.sessions.get()
            if (googleSession != null) {
                val userInfo: UserInfo = httpClient.get("https://www.googleapis.com/oauth2/v2/userinfo") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer ${googleSession.token}")
                    }
                }.body()
                var user = userService.findByEmail(userInfo.email)
                if (user == null) {
                    user = userService.registerUser(userInfo.email, userInfo.email.substringBefore("@"), "${userInfo.givenName} ${userInfo.familyName}")
                }
                call.sessions.set(UserSession(user.id))
                call.respondText("Hello, ${userInfo.name}!")
            } else {
                val redirectUrl = URLBuilder("http://localhost:8080/login").run {
                    parameters.append("redirectUrl", call.request.uri)
                    build()
                }
                call.respondRedirect(redirectUrl)
            }
        }

        albumRoutes()
        photoRoutes()
        userRoutes()
        authenticate("auth-oauth-google") {
            get("/login") {}

            get("/callback") {
                val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
                call.sessions.set(GoogleSession(principal!!.state!!, principal.accessToken))
                val redirect = redirects[principal.state!!]
                call.respondRedirect("/fillUserInfo")
            }
        }
    }
}


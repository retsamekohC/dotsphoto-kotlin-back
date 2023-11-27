package com.dotsphoto.api.controllers

import com.dotsphoto.plugins.SecurityConsts
import com.dotsphoto.plugins.UserSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.ThreadContextElement
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.RuntimeException
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.reflect.KFunction2

fun Route.authAndCall(
    method: KFunction2<String, suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit, Route>,
    path: String,
    body: suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit,
): Route {
    val extendedBody: suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit = {
        val pipelineContext = this
        val connection = DotsphotoConnection(call)
        connection.session<UserSession>()
        withContext(ConnectionContext(connection = connection)) { body(pipelineContext, Unit) }
    }
    return method.invoke(path, extendedBody)
}

class ConnectionContext(val connection: DotsphotoConnection) : AbstractCoroutineContextElement(Key),
    ThreadContextElement<DotsphotoConnection> {
    companion object Key : CoroutineContext.Key<ConnectionContext>

    private val authSessionThreadLocal = ThreadLocal.withInitial { connection }

    override fun restoreThreadContext(context: CoroutineContext, oldState: DotsphotoConnection) {
        authSessionThreadLocal.set(oldState)
    }

    override fun updateThreadContext(context: CoroutineContext): DotsphotoConnection {
        val oldState = authSessionThreadLocal.get()
        authSessionThreadLocal.set(connection)
        return oldState
    }

    override fun toString(): String = "ConnectionContext(${connection})"
}

suspend fun authSession(): UserSession = coroutineContext[ConnectionContext.Key]?.connection?.session()
    ?: throw RuntimeException("No such session")

@JvmInline
value class DotsphotoConnection(val call: ApplicationCall) {
    inline fun <reified T : Principal> session(): T? = call.principal<T>()
}

suspend fun <T> ApplicationCall.getParameter(name: String, mapper: (String) -> T): T {
    val parameter = parameters[name] ?: run {
        this.respond(HttpStatusCode.BadRequest)
        throw BadRequestException("No parameter named $name in call ${request.uri}")
    }
    return runCatching {
        mapper.invoke(parameter)
    }.getOrElse {
        this.respond(HttpStatusCode.BadRequest)
        throw BadRequestException("No parameter named $name in call ${this.request.uri}")
    }
}
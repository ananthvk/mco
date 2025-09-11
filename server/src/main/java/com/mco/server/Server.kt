package com.mco.server

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.calllogging.*
import org.slf4j.event.Level

import io.ktor.http.HttpStatusCode

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) { json() }
        install(CallLogging) {
            level = Level.INFO
        }
        TaskRegistry.register(PiTask)

        routing {
            get("/health") {
                call.respondText("OK")
            }
            post("/offload/{task}") {
                val taskName = call.parameters["task"] ?: return@post call.respond(HttpStatusCode.NotFound)
                val task = TaskRegistry.get(taskName) ?: return@post call.respond(HttpStatusCode.NotFound)
                val params = call.receive<Map<String,String>>()
                call.respond(task.execute(params))
            }
        }
    }.start(wait = true)
}
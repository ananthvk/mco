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

import com.mco.shared.PiReq
import com.mco.shared.PiRes
import com.mco.shared.monteCarloPi

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) { json() }
        install(CallLogging) {
            level = Level.INFO
        }
        routing {
            get("/health") {
                call.respondText("OK")
            }
            post("/offload/pi") {
                val req = call.receive<PiReq>()
                val start = System.currentTimeMillis()
                val result = monteCarloPi(req.iterations, req.seed)
                val took = System.currentTimeMillis() - start
                call.respond(PiRes(pi = result, durationMs = took))
            }
        }
    }.start(wait = true)
}
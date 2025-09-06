package com.mco.server

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.Serializable
import kotlin.random.Random
import io.ktor.server.plugins.calllogging.*
import org.slf4j.event.Level


@Serializable data class PiReq(val iterations: Int, val seed: Long)
@Serializable data class PiRes(val pi: Double, val durationMs: Long)

fun monteCarloPi(iterations: Int, seed: Long): Double {
    val rnd = Random(seed)
    var inside = 0
    repeat(iterations) {
        val x = rnd.nextDouble()
        val y = rnd.nextDouble()
        if (x * x + y * y <= 1.0) inside++
    }
    return 4.0 * inside / iterations
}
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
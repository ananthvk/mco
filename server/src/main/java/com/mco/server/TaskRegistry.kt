package com.mco.server

import com.mco.shared.Offloadable
import kotlinx.serialization.json.*
import kotlinx.serialization.encodeToString
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

object TaskRegistry {
    private val tasks = mutableMapOf<String, Offloadable<*>>()

    fun register(task: Offloadable<*>) {
        println("Registering task: ${task.name}")
        tasks[task.name] = task
    }

    suspend fun execute(call: ApplicationCall, name: String, params: Map<String,String>) {
        val task = tasks[name] ?: return call.respond(HttpStatusCode.NotFound)
        val result = task.run(params)

        val jsonText = when(result) {
            null -> "null"
            is String, is Number, is Boolean -> Json.encodeToString(JsonPrimitive(result.toString()))
            is Map<*, *> -> Json.encodeToString(
                JsonObject.serializer(),
                JsonObject(result.entries
                    .filter { it.key is String }
                    .associate { it.key as String to Json.encodeToJsonElement(it.value) })
            )
            is Iterable<*> -> Json.encodeToString(
                JsonArray.serializer(),
                JsonArray(result.map { Json.encodeToJsonElement(it) })
            )
            else -> {
                // If the result is a @Serializable data class (e.g. PiResult), encode with its serializer
                try {
                    val kClass = result::class
                    val serializer = kotlinx.serialization.serializer(kClass.java)
                    Json.encodeToString(serializer, result)
                } catch (_: Exception) {
                    // fallback for unknown types: convert to JSON object via reflection
                    val props = result::class.members
                        .filterIsInstance<kotlin.reflect.KProperty1<Any, *>>()
                        .associate { it.name to Json.encodeToJsonElement(it.get(result)) }
                    Json.encodeToString(JsonObject.serializer(), JsonObject(props))
                }
            }
        }

        call.respondText(jsonText, ContentType.Application.Json)
    }
}

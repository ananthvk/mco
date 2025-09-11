package com.mco.shared

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend inline fun <reified T> offloadOrLocal(
    client: HttpClient,
    serverBase: String,
    task: Offloadable<T>,
    params: Map<String,String>,
    auto: Boolean = true
): T {
    val remote = auto && shouldOffload(params)
    return if (remote) {
        client.post("$serverBase/offload/${task.name}") {
            contentType(ContentType.Application.Json)
            setBody(params)
        }.body()
    } else {
        withContext(Dispatchers.Default) { task.run(params) }
    }
}

// simple stub; improve later
fun shouldOffload(params: Map<String,String>) = true

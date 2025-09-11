package com.mco.shared

interface Offloadable<T> {
    val name: String
    suspend fun run(params: Map<String,String>): T
}

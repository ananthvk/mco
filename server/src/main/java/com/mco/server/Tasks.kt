package com.mco.server

import com.mco.shared.*

interface OffloadTask {
    val name: String
    suspend fun execute(params: Map<String, String>): Any
}

object TaskRegistry {
    private val tasks = mutableMapOf<String, OffloadTask>()
    fun register(task: OffloadTask) { tasks[task.name] = task }
    fun get(name: String) = tasks[name]
}

object PiTask : OffloadTask {
    override val name = "pi"
    override suspend fun execute(params: Map<String,String>): Any {
        val iterations = params["iterations"]?.toInt() ?: 10000
        val seed = params["seed"]?.toLong() ?: 1234L
        val start = System.currentTimeMillis()
        val pi = monteCarloPi(iterations, seed)
        val took = System.currentTimeMillis() - start
        return PiRes(pi, took)
    }
}

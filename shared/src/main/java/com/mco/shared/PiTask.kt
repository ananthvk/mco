package com.mco.shared

import kotlin.random.Random

object PiTask : Offloadable<PiResult> {
    override val name = "pi"
    override suspend fun run(params: Map<String,String>): PiResult {
        val iterations = params["iterations"]?.toInt() ?: 10_000
        val seed  = params["seed"]?.toLong() ?: 1234L
        val start = System.currentTimeMillis()
        val rnd = Random(seed)
        var inside = 0
        repeat(iterations) {
            val x = rnd.nextDouble()
            val y = rnd.nextDouble()
            if (x*x + y*y <= 1.0) inside++
        }
        val pi = 4.0 * inside / iterations
        val took = System.currentTimeMillis() - start
        return PiResult(pi, took)
    }
}

@kotlinx.serialization.Serializable
data class PiResult(val pi: Double, val durationMs: Long)

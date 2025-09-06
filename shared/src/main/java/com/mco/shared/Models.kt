package com.mco.shared

import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class PiReq(val iterations: Int, val seed: Long)

@Serializable
data class PiRes(val pi: Double, val durationMs: Long)

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

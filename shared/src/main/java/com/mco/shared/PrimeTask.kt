package com.mco.shared

import kotlinx.serialization.Serializable
import kotlin.math.sqrt

// Computationally heavy task: calculate all prime numbers up to a large N
object PrimeCalculationTask : Offloadable<PrimeCalculationResult> {
    override val name = "prime_calc"

    override suspend fun run(params: Map<String, String>): PrimeCalculationResult {
        // TODO: Instead of throwing error, return error to client
        val nStr = params["n"] ?: throw IllegalArgumentException("Parameter 'n' is required")
        val n = nStr.toIntOrNull() ?: throw IllegalArgumentException("Parameter 'n' must be a valid integer")
        val primes = mutableListOf<Int>()
        for (i in 2..n) {
            var isPrime = true
            val limit = sqrt(i.toDouble()).toInt()
            for (j in 2..limit) {
                if (i % j == 0) {
                    isPrime = false
                    break
                }
            }
            if (isPrime) primes.add(i)
        }
        return PrimeCalculationResult(n, primes.size)
    }
}

@Serializable
data class PrimeCalculationResult(val upTo: Int, val primeCount: Int)
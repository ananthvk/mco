package com.mco.mobilecloudoffloading
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mco.shared.PiResult
import com.mco.shared.PiTask
import com.mco.shared.PrimeCalculationResult
import com.mco.shared.PrimeCalculationTask
import com.mco.shared.offloadOrLocal
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import io.ktor.http.contentType

import io.ktor.http.ContentType



class MainActivity : AppCompatActivity() {
    private val client by lazy { HttpClient(Android) { install(ContentNegotiation) { json() } } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnLocal = findViewById<Button>(R.id.btnLocal)
        val btnRemote = findViewById<Button>(R.id.btnRemote)
        val tvResult = findViewById<TextView>(R.id.tvResult)
        val tbServerIP = findViewById<EditText>(R.id.tbServerIP)
        val tbIterations = findViewById<EditText>(R.id.tbIterations)
        val tbSeed = findViewById<EditText>(R.id.tbSeed)

        btnLocal.setOnClickListener {
            val iterations = tbIterations.text.toString().toIntOrNull() ?: 10_000
            val seed = tbSeed.text.toString().toLongOrNull() ?: 1234L
            lifecycleScope.launch { runLocal(iterations, seed, tvResult) }
        }

        btnRemote.setOnClickListener {
            val serverBase = tbServerIP.text.toString().ifBlank { "http://10.0.2.2:8080" }
            val iterations = tbIterations.text.toString().toIntOrNull() ?: 10_000
            val seed = tbSeed.text.toString().toLongOrNull() ?: 1234L
            lifecycleScope.launch { runRemote(serverBase, iterations, seed, tvResult) }
        }

        val tbPrimesUpto = findViewById<EditText>(R.id.tbPrimes)
        val btnLocal2 = findViewById<Button>(R.id.btnLocal4)
        val btnRemote2 = findViewById<Button>(R.id.btnRemote4)
        val tvResult2 = findViewById<TextView>(R.id.tvResult4)


        btnLocal2.setOnClickListener {
            val n = tbPrimesUpto.text.toString().toIntOrNull() ?: 1234
            lifecycleScope.launch { runLocal2(n, tvResult2) }
        }

        btnRemote2.setOnClickListener {
            val serverBase = tbServerIP.text.toString().ifBlank { "http://10.0.2.2:8080" }
            val n = tbPrimesUpto.text.toString().toIntOrNull() ?: 1234
            lifecycleScope.launch { runRemote2(serverBase, n, tvResult2) }
        }

    }

    private suspend fun runLocal(iterations: Int, seed: Long, tv: TextView) {
        tv.text = "Local running..."
        try {
            val res: PiResult = offloadOrLocal(client,
                "http://127.0.0.1",
                PiTask,
                mapOf("iterations" to iterations.toString(), "seed" to seed.toString()),
                false
            )
            tv.text = "Local done in ${res.durationMs}ms\nπ ≈ ${res.pi}"
        } catch (e: Exception) {
            tv.text = "Local failed: ${e.localizedMessage}"
        }
    }

    private suspend fun runRemote(serverBase: String, iterations: Int, seed: Long, tv: TextView) {
        tv.text = "Remote running..."
        try {
            val res: PiResult = offloadOrLocal(client,
                serverBase,
                PiTask,
                mapOf("iterations" to iterations.toString(), "seed" to seed.toString()))
            
            tv.text = "Remote done in ${res.durationMs}ms\nπ ≈ ${res.pi}"
        } catch (e: Exception) {
            tv.text = "Remote failed: ${e.localizedMessage}"
        }
    }
    private suspend fun runLocal2(n: Int, tv: TextView) {
        tv.text = "Local running..."
        try {
            val res: PrimeCalculationResult = offloadOrLocal(client,
                "http://127.0.0.1",
                PrimeCalculationTask,
                mapOf("n" to n.toString()),
                false
            )
            tv.text = "Local done in ${res.durationMs}ms\nThere are ${res.primeCount} primes upto ${res.upTo}"
        } catch (e: Exception) {
            tv.text = "Local failed: ${e.localizedMessage}"
        }
    }

    private suspend fun runRemote2(serverBase: String, n: Int, tv: TextView) {
        tv.text = "Remote running..."
        try {
            val res: PrimeCalculationResult = offloadOrLocal(client,
                serverBase,
                PrimeCalculationTask,
                mapOf("n" to n.toString())
            )
            tv.text = "Remote done in ${res.durationMs}ms\nThere are ${res.primeCount} primes upto ${res.upTo}"
        } catch (e: Exception) {
            tv.text = "Remote failed: ${e.localizedMessage}"
        }
    }
}

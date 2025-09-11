package com.mco.mobilecloudoffloading
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
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

import com.mco.shared.PiReq
import com.mco.shared.PiRes
import com.mco.shared.monteCarloPi
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
    }

    private suspend fun runLocal(iterations: Int, seed: Long, tv: TextView) {
        tv.text = "Local running..."
        val start = System.currentTimeMillis()
        val pi = withContext(Dispatchers.Default) { monteCarloPi(iterations, seed) }
        val took = System.currentTimeMillis() - start
        tv.text = "Local done in ${took}ms\nπ ≈ $pi"
    }

    private suspend fun runRemote(serverBase: String, iterations: Int, seed: Long, tv: TextView) {
        tv.text = "Remote running..."
        try {
            val res: PiRes = client.post("$serverBase/offload/pi") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("iterations" to iterations.toString(), "seed" to seed.toString()))
            }.body()

            tv.text = "Remote done in ${res.durationMs}ms\nπ ≈ ${res.pi}"
        } catch (e: Exception) {
            tv.text = "Remote failed: ${e.localizedMessage}"
        }
    }
}

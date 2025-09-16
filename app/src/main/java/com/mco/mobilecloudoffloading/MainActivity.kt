package com.ananth.mco

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var spinnerOperation: Spinner
    private lateinit var layoutPi: LinearLayout
    private lateinit var layoutPrime: LinearLayout
    private lateinit var tvResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinnerOperation = findViewById(R.id.spinnerOperation)
        layoutPi = findViewById(R.id.layoutPi)
        layoutPrime = findViewById(R.id.layoutPrime)
        tvResult = findViewById(R.id.tvResult)

        val btnPiOption1: Button = findViewById(R.id.btnPiOption1)
        val btnPiOption2: Button = findViewById(R.id.btnPiOption2)
        val btnPrimeOption1: Button = findViewById(R.id.btnPrimeOption1)
        val btnPrimeOption2: Button = findViewById(R.id.btnPrimeOption2)

        // Handle dropdown selection
        spinnerOperation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                when (position) {
                    0 -> { // Pi Operation
                        layoutPi.visibility = View.VISIBLE
                        layoutPrime.visibility = View.GONE
                    }
                    1 -> { // Prime Operation
                        layoutPi.visibility = View.GONE
                        layoutPrime.visibility = View.VISIBLE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Pi buttons
        btnPiOption1.setOnClickListener {
            runOperation("pi", "option1")
        }
        btnPiOption2.setOnClickListener {
            runOperation("pi", "option2")
        }

        // Prime buttons
        btnPrimeOption1.setOnClickListener {
            runOperation("prime", "option1")
        }
        btnPrimeOption2.setOnClickListener {
            runOperation("prime", "option2")
        }
    }

    private fun runOperation(operation: String, option: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Example URL: adjust server address + port
                val url = URL("http://10.0.2.2:8080/$operation?option=$option")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val response = connection.inputStream.bufferedReader().readText()
                withContext(Dispatchers.Main) {
                    tvResult.text = "Result: $response"
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    tvResult.text = "Error: ${e.message}"
                }
            }
        }
    }
}

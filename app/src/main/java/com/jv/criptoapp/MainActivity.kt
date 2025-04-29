package com.jv.criptoapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var etCryptoName: EditText
    private lateinit var btnBuscar: Button
    private lateinit var tvResultado: TextView

    private val client = OkHttpClient() // Cliente HTTP para fazer requisições

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ligando os elementos XML no Kotlin
        etCryptoName = findViewById(R.id.etCryptoName)
        btnBuscar = findViewById(R.id.btnBuscar)
        tvResultado = findViewById(R.id.tvResultado)

        btnBuscar.setOnClickListener {
            val nome = etCryptoName.text.toString().lowercase()
            buscarCriptomoeda(nome)
        }
    }

    private fun buscarCriptomoeda(nome: String) {
        val url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids=$nome"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    tvResultado.text = "Erro ao buscar: ${e.message}"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val json = response.body?.string()
                    val jsonArray = JSONArray(json)
                    if (jsonArray.length() == 0) {
                        runOnUiThread {
                            tvResultado.text = "Criptomoeda não encontrada."
                        }
                    } else {
                        val item = jsonArray.getJSONObject(0)
                        val nome = item.getString("name")
                        val preco = item.getDouble("current_price")
                        val variacao = item.getDouble("price_change_percentage_24h")
                        val marketCap = item.getLong("market_cap")

                        val texto = """
                            Nome: $nome
                            Preço: $preco USD
                            Variação 24h: $variacao%
                            Market Cap: $marketCap USD
                        """.trimIndent()

                        runOnUiThread {
                            tvResultado.text = texto
                        }
                    }
                }
            }
        })
    }
}

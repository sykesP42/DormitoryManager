package com.dormitorymanager

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class NetworkReceiveActivity : AppCompatActivity() {

    private lateinit var etServerUrl: EditText
    private lateinit var btnConnect: MaterialButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_receive)

        initViews()
    }

    private fun initViews() {
        etServerUrl = findViewById(R.id.etServerUrl)
        btnConnect = findViewById(R.id.btnConnect)
        progressBar = findViewById(R.id.progressBar)

        findViewById<android.widget.ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }

        btnConnect.setOnClickListener {
            val url = etServerUrl.text.toString().trim()
            if (url.isEmpty()) {
                Toast.makeText(this, "请输入服务器地址", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            fetchData(url)
        }
    }

    private fun fetchData(serverUrl: String) {
        progressBar.visibility = android.view.View.VISIBLE
        btnConnect.isEnabled = false

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val downloadUrl = if (serverUrl.endsWith("/")) {
                    "${serverUrl}download"
                } else {
                    "$serverUrl/download"
                }

                val url = URL(downloadUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()

                    withContext(Dispatchers.Main) {
                        progressBar.visibility = android.view.View.GONE
                        btnConnect.isEnabled = true
                        
                        val intent = Intent(this@NetworkReceiveActivity, ImportConfirmActivity::class.java)
                        intent.putExtra("data_json", response.toString())
                        startActivity(intent)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        progressBar.visibility = android.view.View.GONE
                        btnConnect.isEnabled = true
                        Toast.makeText(this@NetworkReceiveActivity, "服务器响应错误: $responseCode", Toast.LENGTH_LONG).show()
                    }
                }
                connection.disconnect()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = android.view.View.GONE
                    btnConnect.isEnabled = true
                    Toast.makeText(this@NetworkReceiveActivity, "连接失败: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

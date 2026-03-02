package com.dormitorymanager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.IHTTPSession
import fi.iki.elonen.NanoHTTPD.Response
import java.io.IOException

class NetworkShareActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var prefs: PreferencesHelper
    private lateinit var tvServerStatus: TextView
    private lateinit var tvServerUrl: TextView
    private lateinit var btnStartServer: MaterialButton

    private var server: SimpleHttpServer? = null
    private val SERVER_PORT = 8080
    private val PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_share)

        db = DatabaseHelper(this)
        prefs = PreferencesHelper(this)
        tvServerStatus = findViewById(R.id.tvServerStatus)
        tvServerUrl = findViewById(R.id.tvServerUrl)
        btnStartServer = findViewById(R.id.btnStartServer)

        findViewById<android.widget.ImageView>(R.id.ivBack).setOnClickListener {
            stopServer()
            finish()
        }

        btnStartServer.setOnClickListener {
            if (server == null || !server!!.isAlive) {
                checkPermissionsAndStartServer()
            } else {
                stopServer()
            }
        }
    }

    private fun checkPermissionsAndStartServer() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.INTERNET
        )

        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), PERMISSION_REQUEST_CODE)
        } else {
            startServer()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                startServer()
            } else {
                Toast.makeText(this, "需要权限才能启动服务器", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startServer() {
        try {
            server = SimpleHttpServer(SERVER_PORT, db, prefs)
            server?.start()
            
            updateServerStatus(true)
            Toast.makeText(this, "服务器已启动！", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(this, "启动服务器失败: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun stopServer() {
        server?.stop()
        server = null
        updateServerStatus(false)
    }

    private fun updateServerStatus(isRunning: Boolean) {
        if (isRunning) {
            tvServerStatus.text = "服务器正在运行"
            tvServerStatus.setTextColor(getColor(R.color.accent))
            
            val ipAddress = getLocalIpAddress()
            val url = "http://$ipAddress:$SERVER_PORT"
            tvServerUrl.text = url
            tvServerUrl.visibility = android.view.View.VISIBLE
            
            btnStartServer.text = "停止服务器"
        } else {
            tvServerStatus.text = "服务器未启动"
            tvServerStatus.setTextColor(getColor(R.color.text_secondary))
            tvServerUrl.visibility = android.view.View.GONE
            btnStartServer.text = "启动服务器"
        }
    }

    private fun getLocalIpAddress(): String {
        return try {
            val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            val ipAddress = wifiInfo.ipAddress
            String.format(
                "%d.%d.%d.%d",
                ipAddress and 0xFF,
                ipAddress shr 8 and 0xFF,
                ipAddress shr 16 and 0xFF,
                ipAddress shr 24 and 0xFF
            )
        } catch (e: Exception) {
            "127.0.0.1"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopServer()
    }
}

class SimpleHttpServer(
    port: Int,
    private val db: DatabaseHelper,
    private val prefs: PreferencesHelper
) : NanoHTTPD(port) {

    private val gson = Gson()

    override fun serve(session: IHTTPSession): Response {
        return when (session.uri) {
            "/" -> serveHomePage()
            "/download" -> serveDownload()
            else -> newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not Found")
        }
    }

    private fun serveHomePage(): Response {
        val html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>宿舍数据同步</title>
                <style>
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                        background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
                        color: white;
                        min-height: 100vh;
                    }
                    .container {
                        background: rgba(30, 41, 59, 0.9);
                        border-radius: 20px;
                        padding: 30px;
                        box-shadow: 0 10px 40px rgba(0,0,0,0.3);
                    }
                    h1 {
                        text-align: center;
                        color: #6366f1;
                        margin-bottom: 30px;
                    }
                    .btn {
                        display: block;
                        width: 100%;
                        padding: 15px;
                        background: #6366f1;
                        color: white;
                        border: none;
                        border-radius: 12px;
                        font-size: 18px;
                        cursor: pointer;
                        text-decoration: none;
                        text-align: center;
                        margin-top: 20px;
                        transition: background 0.3s;
                    }
                    .btn:hover {
                        background: #4f46e5;
                    }
                    .info {
                        text-align: center;
                        color: #94a3b8;
                        margin-top: 20px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>宿舍数据同步</h1>
                    <p style="text-align: center; color: #cbd5e1;">点击下方按钮下载宿舍数据</p>
                    <a href="/download" class="btn">下载数据</a>
                    <p class="info">下载后在应用中导入即可</p>
                </div>
            </body>
            </html>
        """.trimIndent()
        
        return newFixedLengthResponse(Response.Status.OK, "text/html", html)
    }

    private fun serveDownload(): Response {
        val students = db.getStudents()
        val duties = db.getAllDuties()
        val dutyRecords = db.getAllDutyRecords()
        val data = BackupData(
            dormitoryName = prefs.dormitoryName,
            dormitorySize = prefs.dormitorySize,
            startDate = prefs.startDate,
            reminderEnabled = prefs.reminderEnabled,
            reminderHour = prefs.reminderHour,
            reminderMinute = prefs.reminderMinute,
            students = students,
            duties = duties,
            dutyRecords = dutyRecords
        )
        val json = gson.toJson(data)
        
        val response = newFixedLengthResponse(Response.Status.OK, "application/json", json)
        response.addHeader("Content-Disposition", "attachment; filename=dormitory_data.json")
        return response
    }
}

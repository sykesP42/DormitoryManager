package com.dormitorymanager

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class SyncActivity : AppCompatActivity() {

    private lateinit var cvQrShare: CardView
    private lateinit var cvNetworkShare: CardView
    private lateinit var cvQrReceive: CardView
    private lateinit var cvNetworkReceive: CardView

    private val CAMERA_PERMISSION_REQUEST_CODE = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync)

        initViews()
    }

    private fun initViews() {
        findViewById<android.widget.ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }

        cvQrShare = findViewById(R.id.cvQrShare)
        cvNetworkShare = findViewById(R.id.cvNetworkShare)
        cvQrReceive = findViewById(R.id.cvQrReceive)
        cvNetworkReceive = findViewById(R.id.cvNetworkReceive)

        cvQrShare.setOnClickListener {
            startActivity(Intent(this, QrDisplayActivity::class.java))
        }

        cvNetworkShare.setOnClickListener {
            startActivity(Intent(this, NetworkShareActivity::class.java))
        }

        cvQrReceive.setOnClickListener {
            checkCameraPermissionAndScan()
        }

        cvNetworkReceive.setOnClickListener {
            startActivity(Intent(this, NetworkReceiveActivity::class.java))
        }
    }

    private fun checkCameraPermissionAndScan() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            startQrScanner()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startQrScanner()
            } else {
                Toast.makeText(this, "需要相机权限才能扫描QR码", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startQrScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("请扫描宿舍数据QR码")
        integrator.setCameraId(0)
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(false)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                val intent = Intent(this, ImportConfirmActivity::class.java)
                intent.putExtra("data_json", result.contents)
                startActivity(intent)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}

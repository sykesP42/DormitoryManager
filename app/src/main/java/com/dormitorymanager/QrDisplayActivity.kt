package com.dormitorymanager

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.util.Hashtable

class QrDisplayActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var prefs: PreferencesHelper
    private lateinit var ivQrCode: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_display)

        db = DatabaseHelper(this)
        prefs = PreferencesHelper(this)
        ivQrCode = findViewById(R.id.ivQrCode)

        findViewById<android.widget.ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }

        generateQrCode()
    }

    private fun generateQrCode() {
        try {
            val students = db.getStudents()
            val duties = db.getAllDuties()
            val dutyRecords = db.getAllDutyRecords()
            val gson = Gson()
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
            val bitmap = generateQrBitmap(json)
            ivQrCode.setImageBitmap(bitmap)
        } catch (e: Exception) {
            Toast.makeText(this, "生成QR码失败: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun generateQrBitmap(content: String): Bitmap? {
        val size = 1024
        val hints = Hashtable<EncodeHintType, Any>()
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        hints[EncodeHintType.MARGIN] = 2

        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)

        val pixels = IntArray(size * size)
        for (y in 0 until size) {
            val offset = y * size
            for (x in 0 until size) {
                pixels[offset + x] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
            }
        }

        return Bitmap.createBitmap(pixels, size, size, Bitmap.Config.RGB_565)
    }
}

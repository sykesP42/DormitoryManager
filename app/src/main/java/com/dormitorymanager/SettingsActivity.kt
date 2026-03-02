package com.dormitorymanager

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class SettingsActivity : AppCompatActivity() {

    private lateinit var prefs: PreferencesHelper
    private var selectedStartDate: LocalDate? = null

    private lateinit var etDormitoryName: EditText
    private lateinit var tvStartDate: TextView
    private lateinit var tvManageRoommates: TextView
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        prefs = PreferencesHelper(this)
        initViews()
        loadSettings()
    }

    private fun initViews() {
        etDormitoryName = findViewById(R.id.etDormitoryName)
        tvStartDate = findViewById(R.id.tvStartDate)
        tvManageRoommates = findViewById(R.id.tvManageRoommates)
        btnSave = findViewById(R.id.btnSave)

        findViewById<android.widget.ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }

        tvStartDate.setOnClickListener {
            showDatePicker()
        }

        tvManageRoommates.setOnClickListener {
            Toast.makeText(this, "室友管理功能开发中", Toast.LENGTH_SHORT).show()
        }

        btnSave.setOnClickListener {
            saveSettings()
        }
    }

    private fun loadSettings() {
        etDormitoryName.setText(prefs.dormitoryName)
        
        prefs.startDate?.let {
            selectedStartDate = LocalDate.parse(it)
            val formatter = DateTimeFormatter.ofPattern("yyyy年M月d日")
            tvStartDate.text = selectedStartDate!!.format(formatter)
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedStartDate = LocalDate.of(year, month + 1, dayOfMonth)
                val formatter = DateTimeFormatter.ofPattern("yyyy年M月d日")
                tvStartDate.text = selectedStartDate!!.format(formatter)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun saveSettings() {
        val name = etDormitoryName.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(this, "请输入宿舍名称", Toast.LENGTH_SHORT).show()
            return
        }

        prefs.dormitoryName = name
        
        selectedStartDate?.let {
            prefs.startDate = it.format(DateTimeFormatter.ISO_LOCAL_DATE)
        }

        Toast.makeText(this, "保存成功！", Toast.LENGTH_SHORT).show()
        finish()
    }
}

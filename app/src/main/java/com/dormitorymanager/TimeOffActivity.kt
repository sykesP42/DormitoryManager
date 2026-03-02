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

class TimeOffActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private var selectedDate: LocalDate = LocalDate.now()
    private var selectedMakeupDate: LocalDate? = null
    private var currentDutyStudent: Student? = null

    private lateinit var tvDate: TextView
    private lateinit var tvCurrentDuty: TextView
    private lateinit var tvMakeupDate: TextView
    private lateinit var etNotes: EditText
    private lateinit var btnConfirm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_off)

        db = DatabaseHelper(this)
        initViews()
        loadData()
    }

    private fun initViews() {
        tvDate = findViewById(R.id.tvDate)
        tvCurrentDuty = findViewById(R.id.tvCurrentDuty)
        tvMakeupDate = findViewById(R.id.tvMakeupDate)
        etNotes = findViewById(R.id.etNotes)
        btnConfirm = findViewById(R.id.btnConfirm)

        findViewById<android.widget.ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }

        tvDate.setOnClickListener {
            showDatePicker(true)
        }

        tvMakeupDate.setOnClickListener {
            showDatePicker(false)
        }

        btnConfirm.setOnClickListener {
            confirmTimeOff()
        }

        updateDateUI()
    }

    private fun loadData() {
        updateCurrentDuty()
    }

    private fun showDatePicker(isMainDate: Boolean) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val date = LocalDate.of(year, month + 1, dayOfMonth)
                if (isMainDate) {
                    selectedDate = date
                    updateDateUI()
                    updateCurrentDuty()
                } else {
                    selectedMakeupDate = date
                    updateMakeupDateUI()
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun updateDateUI() {
        val formatter = DateTimeFormatter.ofPattern("yyyy年M月d日")
        tvDate.text = selectedDate.format(formatter)
    }

    private fun updateMakeupDateUI() {
        selectedMakeupDate?.let {
            val formatter = DateTimeFormatter.ofPattern("yyyy年M月d日")
            tvMakeupDate.text = it.format(formatter)
        }
    }

    private fun updateCurrentDuty() {
        val dateStr = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val (dutyStudent, _) = db.getDutyByDate(dateStr)
        currentDutyStudent = dutyStudent
        tvCurrentDuty.text = dutyStudent?.name ?: "无"
    }

    private fun confirmTimeOff() {
        if (currentDutyStudent == null) {
            Toast.makeText(this, "该日期无值日安排", Toast.LENGTH_SHORT).show()
            return
        }

        val dateStr = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val notes = etNotes.text.toString()

        val duty = Duty(
            studentId = currentDutyStudent!!.id,
            date = dateStr,
            type = "timeoff",
            notes = if (selectedMakeupDate != null) {
                val makeupDateStr = selectedMakeupDate!!.format(DateTimeFormatter.ISO_LOCAL_DATE)
                "补值日: $makeupDateStr $notes"
            } else {
                notes
            }
        )
        db.insertDuty(duty)

        Toast.makeText(this, "调休成功！", Toast.LENGTH_SHORT).show()
        finish()
    }
}

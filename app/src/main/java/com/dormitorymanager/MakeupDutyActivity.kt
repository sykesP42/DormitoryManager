package com.dormitorymanager

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class MakeupDutyActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private var students: List<Student> = emptyList()
    private var selectedDate: LocalDate? = null
    private var selectedStudent: Student? = null

    private lateinit var rvMakeupStudents: RecyclerView
    private lateinit var tvDate: TextView
    private lateinit var etNotes: EditText
    private lateinit var btnConfirm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_makeup_duty)

        db = DatabaseHelper(this)
        initViews()
        loadData()
    }

    private fun initViews() {
        rvMakeupStudents = findViewById(R.id.rvMakeupStudents)
        tvDate = findViewById(R.id.tvDate)
        etNotes = findViewById(R.id.etNotes)
        btnConfirm = findViewById(R.id.btnConfirm)

        findViewById<android.widget.ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }

        tvDate.setOnClickListener {
            showDatePicker()
        }

        btnConfirm.setOnClickListener {
            confirmMakeup()
        }

        rvMakeupStudents.layoutManager = LinearLayoutManager(this)
    }

    private fun loadData() {
        students = db.getStudents()
        updateStudentsList()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                updateDateUI()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun updateDateUI() {
        selectedDate?.let {
            val formatter = DateTimeFormatter.ofPattern("yyyy年M月d日")
            tvDate.text = it.format(formatter)
        }
    }

    private fun updateStudentsList() {
        val adapter = StudentAdapter(
            students = students,
            onItemClick = { student ->
                selectedStudent = student
                Toast.makeText(this, "已选择 ${student.name}", Toast.LENGTH_SHORT).show()
            }
        )
        rvMakeupStudents.adapter = adapter
    }

    private fun confirmMakeup() {
        if (selectedStudent == null) {
            Toast.makeText(this, "请选择补值日人", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedDate == null) {
            Toast.makeText(this, "请选择补值日日期", Toast.LENGTH_SHORT).show()
            return
        }

        val dateStr = selectedDate!!.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val notes = etNotes.text.toString()

        val duty = Duty(
            studentId = selectedStudent!!.id,
            date = dateStr,
            type = "makeup",
            notes = notes
        )
        db.insertDuty(duty)

        Toast.makeText(this, "补值日登记成功！", Toast.LENGTH_SHORT).show()
        finish()
    }
}

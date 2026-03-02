package com.dormitorymanager

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class SwapDutyActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private var students: List<Student> = emptyList()
    private var selectedDate: LocalDate = LocalDate.now()
    private var selectedStudent: Student? = null
    private var currentDutyStudent: Student? = null

    private lateinit var tvDate: TextView
    private lateinit var tvCurrentDuty: TextView
    private lateinit var rvSwapStudents: RecyclerView
    private lateinit var btnConfirm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swap_duty)

        db = DatabaseHelper(this)
        initViews()
        loadData()
    }

    private fun initViews() {
        tvDate = findViewById(R.id.tvDate)
        tvCurrentDuty = findViewById(R.id.tvCurrentDuty)
        rvSwapStudents = findViewById(R.id.rvSwapStudents)
        btnConfirm = findViewById(R.id.btnConfirm)

        findViewById<android.widget.ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }

        tvDate.setOnClickListener {
            showDatePicker()
        }

        btnConfirm.setOnClickListener {
            confirmSwap()
        }

        rvSwapStudents.layoutManager = LinearLayoutManager(this)
        updateDateUI()
    }

    private fun loadData() {
        students = db.getStudents()
        updateCurrentDuty()
        updateStudentsList()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                updateDateUI()
                updateCurrentDuty()
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

    private fun updateCurrentDuty() {
        val dateStr = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val (dutyStudent, _) = db.getDutyByDate(dateStr)
        currentDutyStudent = dutyStudent
        tvCurrentDuty.text = dutyStudent?.name ?: "无"
        updateStudentsList()
    }

    private fun updateStudentsList() {
        val availableStudents = students.filter { it.id != currentDutyStudent?.id }
        val adapter = StudentAdapter(
            students = availableStudents,
            onItemClick = { student ->
                selectedStudent = student
                Toast.makeText(this, "已选择 ${student.name}", Toast.LENGTH_SHORT).show()
            }
        )
        rvSwapStudents.adapter = adapter
    }

    private fun confirmSwap() {
        if (selectedStudent == null) {
            Toast.makeText(this, "请选择要交换的室友", Toast.LENGTH_SHORT).show()
            return
        }

        val dateStr = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val duty = Duty(
            studentId = selectedStudent!!.id,
            date = dateStr,
            type = "swap",
            notes = "与 ${currentDutyStudent?.name} 交换"
        )
        db.insertDuty(duty)

        Toast.makeText(this, "换班成功！", Toast.LENGTH_SHORT).show()
        finish()
    }
}

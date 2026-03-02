package com.dormitorymanager

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StatisticsActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var students: List<Student>

    private lateinit var tvTotalDays: TextView
    private lateinit var tvTotalSwaps: TextView
    private lateinit var tvTotalTimeoffs: TextView
    private lateinit var tvTotalMakeups: TextView
    private lateinit var rvStudentStats: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        db = DatabaseHelper(this)
        initViews()
        loadStatistics()
    }

    private fun initViews() {
        tvTotalDays = findViewById(R.id.tvTotalDays)
        tvTotalSwaps = findViewById(R.id.tvTotalSwaps)
        tvTotalTimeoffs = findViewById(R.id.tvTotalTimeoffs)
        tvTotalMakeups = findViewById(R.id.tvTotalMakeups)
        rvStudentStats = findViewById(R.id.rvStudentStats)

        findViewById<android.widget.ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }

        rvStudentStats.layoutManager = LinearLayoutManager(this)
    }

    private fun loadStatistics() {
        students = db.getStudents()

        val totalDays = db.getTotalDays()
        val (swapCount, timeoffCount, makeupCount) = db.getStatistics()

        tvTotalDays.text = totalDays.toString()
        tvTotalSwaps.text = swapCount.toString()
        tvTotalTimeoffs.text = timeoffCount.toString()
        tvTotalMakeups.text = makeupCount.toString()

        val studentStats = students.map { student ->
            StudentStatItem(student, db.getStudentDutyCount(student.id))
        }

        val adapter = StudentStatAdapter(studentStats)
        rvStudentStats.adapter = adapter
    }
}

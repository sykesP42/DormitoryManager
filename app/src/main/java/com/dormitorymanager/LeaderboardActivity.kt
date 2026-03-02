package com.dormitorymanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var students: List<Student>

    private lateinit var rvLeaderboard: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        db = DatabaseHelper(this)
        initViews()
        loadLeaderboard()
    }

    private fun initViews() {
        rvLeaderboard = findViewById(R.id.rvLeaderboard)

        findViewById<android.widget.ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }

        rvLeaderboard.layoutManager = LinearLayoutManager(this)
    }

    private fun loadLeaderboard() {
        students = db.getStudents()

        val leaderboardItems = students.map { student ->
            LeaderboardItem(
                rank = 0,
                student = student,
                dutyCount = db.getStudentDutyCount(student.id)
            )
        }.sortedByDescending { it.dutyCount }
            .mapIndexed { index, item ->
                item.copy(rank = index + 1)
            }

        val adapter = LeaderboardAdapter(leaderboardItems)
        rvLeaderboard.adapter = adapter
    }
}

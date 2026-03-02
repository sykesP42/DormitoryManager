package com.dormitorymanager

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var prefs: PreferencesHelper
    private var students: List<Student> = emptyList()
    private var todayDuty: Student? = null

    private lateinit var tvTitle: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvDutyInitial: TextView
    private lateinit var tvDutyName: TextView
    private lateinit var llDutyCircle: LinearLayout
    private lateinit var rvStudents: RecyclerView
    private lateinit var fabCalendar: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DatabaseHelper(this)
        prefs = PreferencesHelper(this)
        initViews()
        loadData()
    }

    override fun onResume() {
        super.onResume()
        loadData()
        updateTitle()
    }

    private fun initViews() {
        tvTitle = findViewById(R.id.tvTitle)
        tvDate = findViewById(R.id.tvDate)
        tvDutyInitial = findViewById(R.id.tvDutyInitial)
        tvDutyName = findViewById(R.id.tvDutyName)
        llDutyCircle = findViewById(R.id.llDutyCircle)
        rvStudents = findViewById(R.id.rvStudents)
        fabCalendar = findViewById(R.id.fabCalendar)

        updateTitle()

        val today = LocalDate.now()
        val weekdays = arrayOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
        val dateText = "${today.monthValue}月${today.dayOfMonth}日 ${weekdays[today.dayOfWeek.value - 1]}"
        tvDate.text = dateText

        rvStudents.layoutManager = LinearLayoutManager(this)

        fabCalendar.setOnClickListener {
            Toast.makeText(this, "日历视图功能开发中", Toast.LENGTH_SHORT).show()
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.cvSwap).setOnClickListener {
            startActivity(Intent(this, SwapDutyActivity::class.java))
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.cvTimeoff).setOnClickListener {
            startActivity(Intent(this, TimeOffActivity::class.java))
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.cvMakeup).setOnClickListener {
            startActivity(Intent(this, MakeupDutyActivity::class.java))
        }

        findViewById<android.widget.ImageView>(R.id.ivSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun updateTitle() {
        tvTitle.text = prefs.dormitoryName
    }

    private fun loadData() {
        students = db.getStudents()
        
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val (dutyStudent, _) = db.getDutyByDate(today)
        todayDuty = dutyStudent

        updateTodayDutyUI()
        updateStudentsList()
    }

    private fun updateTodayDutyUI() {
        todayDuty?.let { student ->
            tvDutyInitial.text = student.name.takeLast(1)
            tvDutyName.text = student.name
            tvDutyInitial.setTextColor(student.color)
            
            llDutyCircle.background = resources.getDrawable(R.drawable.circle_border, null)
            llDutyCircle.background.mutate().setTint(student.color)
        }
    }

    private fun updateStudentsList() {
        val adapter = StudentAdapter(
            students = students,
            todayDutyId = todayDuty?.id,
            onItemClick = { student ->
                Toast.makeText(this, "${student.name} 的详情", Toast.LENGTH_SHORT).show()
            }
        )
        rvStudents.adapter = adapter
    }
}

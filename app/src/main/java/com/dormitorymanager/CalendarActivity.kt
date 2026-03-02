package com.dormitorymanager

import android.app.AlertDialog
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class CalendarActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private var currentMonth: YearMonth = YearMonth.now()

    private lateinit var tvMonthYear: TextView
    private lateinit var rvCalendar: RecyclerView
    private lateinit var calendarAdapter: CalendarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        db = DatabaseHelper(this)
        initViews()
        loadCalendar()
    }

    private fun initViews() {
        tvMonthYear = findViewById(R.id.tvMonthYear)
        rvCalendar = findViewById(R.id.rvCalendar)

        findViewById<android.widget.ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }

        findViewById<android.widget.ImageView>(R.id.ivPrevMonth).setOnClickListener {
            currentMonth = currentMonth.minusMonths(1)
            loadCalendar()
        }

        findViewById<android.widget.ImageView>(R.id.ivNextMonth).setOnClickListener {
            currentMonth = currentMonth.plusMonths(1)
            loadCalendar()
        }

        rvCalendar.layoutManager = GridLayoutManager(this, 7)
    }

    private fun loadCalendar() {
        val formatter = DateTimeFormatter.ofPattern("yyyy年M月")
        tvMonthYear.text = currentMonth.format(formatter)

        val days = mutableListOf<CalendarDayItem>()
        val firstDayOfMonth = currentMonth.atDay(1)
        val lastDayOfMonth = currentMonth.atEndOfMonth()
        
        val dayOfWeekOfFirstDay = firstDayOfMonth.dayOfWeek.value % 7
        
        for (i in 0 until dayOfWeekOfFirstDay) {
            days.add(CalendarDayItem(null, false))
        }

        var date = firstDayOfMonth
        while (!date.isAfter(lastDayOfMonth)) {
            val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val (student, dutyType) = db.getDutyByDate(dateStr)
            days.add(CalendarDayItem(date, true, dutyType, student))
            date = date.plusDays(1)
        }

        val remainingDays = 42 - days.size
        for (i in 0 until remainingDays) {
            days.add(CalendarDayItem(null, false))
        }

        calendarAdapter = CalendarAdapter(days) { selectedDate ->
            showDateDetail(selectedDate)
        }
        rvCalendar.adapter = calendarAdapter
    }

    private fun showDateDetail(date: LocalDate) {
        val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val (student, dutyType) = db.getDutyByDate(dateStr)
        val formatter = DateTimeFormatter.ofPattern("yyyy年M月d日")
        
        val typeText = when (dutyType) {
            "timeoff" -> "调休"
            "makeup" -> "补值日"
            "swap" -> "换班"
            else -> "正常值日"
        }

        val message = if (student != null) {
            "日期：${date.format(formatter)}\n" +
            "值日人员：${student.name}\n" +
            "类型：$typeText"
        } else {
            "日期：${date.format(formatter)}\n" +
            "值日人员：未分配\n" +
            "类型：$typeText"
        }

        AlertDialog.Builder(this)
            .setTitle("值日详情")
            .setMessage(message)
            .setPositiveButton("确定", null)
            .show()
    }
}

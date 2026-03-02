package com.dormitorymanager

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var prefs: PreferencesHelper
    private var students: List<Student> = emptyList()
    private var todayDuty: Student? = null

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var btnMenu: LinearLayout
    private lateinit var tvDate: TextView
    private lateinit var tvDutyInitial: TextView
    private lateinit var tvDutyName: TextView
    private lateinit var llDutyCircle: LinearLayout
    private lateinit var rvStudents: RecyclerView
    private lateinit var rvUpcomingDuties: RecyclerView
    private lateinit var tvCountdown: TextView
    private lateinit var llCountdown: LinearLayout
    private lateinit var btnMarkComplete: LinearLayout
    private lateinit var tvMarkComplete: TextView
    private lateinit var tvCompletedAt: TextView
    private lateinit var llDutyActions: LinearLayout
    private lateinit var btnRate: LinearLayout
    private lateinit var tvRate: TextView
    private lateinit var btnLike: LinearLayout
    private lateinit var tvLikes: TextView
    private lateinit var tvCompletionRate: TextView
    private lateinit var tvAvgRating: TextView
    
    private val handler = Handler(Looper.getMainLooper())
    private var countdownRunnable: Runnable? = null
    private var todayDutyRecord: DutyRecord? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DatabaseHelper(this)
        prefs = PreferencesHelper(this)
        initViews()
        setupDrawer()
        loadData()
    }

    override fun onResume() {
        super.onResume()
        loadData()
        updateNavHeader()
        startCountdown()
    }

    override fun onPause() {
        super.onPause()
        stopCountdown()
    }

    private fun initViews() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        btnMenu = findViewById(R.id.btnMenu)
        tvDate = findViewById(R.id.tvDate)
        tvDutyInitial = findViewById(R.id.tvDutyInitial)
        tvDutyName = findViewById(R.id.tvDutyName)
        llDutyCircle = findViewById(R.id.llDutyCircle)
        rvStudents = findViewById(R.id.rvStudents)
        rvUpcomingDuties = findViewById(R.id.rvUpcomingDuties)
        tvCountdown = findViewById(R.id.tvCountdown)
        llCountdown = findViewById(R.id.llCountdown)
        btnMarkComplete = findViewById(R.id.btnMarkComplete)
        tvMarkComplete = findViewById(R.id.tvMarkComplete)
        tvCompletedAt = findViewById(R.id.tvCompletedAt)
        llDutyActions = findViewById(R.id.llDutyActions)
        btnRate = findViewById(R.id.btnRate)
        tvRate = findViewById(R.id.tvRate)
        btnLike = findViewById(R.id.btnLike)
        tvLikes = findViewById(R.id.tvLikes)
        tvCompletionRate = findViewById(R.id.tvCompletionRate)
        tvAvgRating = findViewById(R.id.tvAvgRating)
        
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        
        btnMarkComplete.setOnClickListener {
            markTodayDutyComplete()
        }
        
        btnRate.setOnClickListener {
            showRatingDialog()
        }
        
        btnLike.setOnClickListener {
            likeTodayDuty()
        }

        val today = LocalDate.now()
        val weekdays = arrayOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
        val dateText = "${today.monthValue}月${today.dayOfMonth}日 ${weekdays[today.dayOfWeek.value - 1]}"
        tvDate.text = dateText

        rvStudents.layoutManager = LinearLayoutManager(this)
        rvUpcomingDuties.layoutManager = LinearLayoutManager(this)

        findViewById<LinearLayout>(R.id.llQuickTimeoff).setOnClickListener {
            startActivity(Intent(this, TimeOffActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.llQuickHistory).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.llQuickLeaderboard).setOnClickListener {
            startActivity(Intent(this, LeaderboardActivity::class.java))
        }
    }

    private fun setupDrawer() {
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_swap -> {
                    startActivity(Intent(this, SwapDutyActivity::class.java))
                }
                R.id.nav_timeoff -> {
                    startActivity(Intent(this, TimeOffActivity::class.java))
                }
                R.id.nav_makeup -> {
                    startActivity(Intent(this, MakeupDutyActivity::class.java))
                }
                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                }
                R.id.nav_statistics -> {
                    startActivity(Intent(this, StatisticsActivity::class.java))
                }
                R.id.nav_leaderboard -> {
                    startActivity(Intent(this, LeaderboardActivity::class.java))
                }
                R.id.nav_calendar -> {
                    startActivity(Intent(this, CalendarActivity::class.java))
                }
                R.id.nav_sync -> {
                    startActivity(Intent(this, SyncActivity::class.java))
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
                R.id.nav_theme -> {
                    showThemeDialog()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        updateNavHeader()
    }

    private fun updateNavHeader() {
        val headerView = navView.getHeaderView(0)
        val tvNavDormName = headerView.findViewById<TextView>(R.id.tvNavDormName)
        tvNavDormName.text = prefs.dormitoryName
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun loadData() {
        students = db.getStudents()
        
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val (dutyStudent, _) = db.getDutyByDate(today)
        todayDuty = dutyStudent

        updateTodayDutyUI()
        updateStudentsList()
        updateUpcomingDutiesUI()
        updateStatistics()
    }

    private fun updateTodayDutyUI() {
        todayDuty?.let { student ->
            tvDutyInitial.text = student.name.takeLast(1)
            tvDutyName.text = student.name
            tvDutyInitial.setTextColor(student.color)
            
            val background = resources.getDrawable(R.drawable.circle_border, null)
            background.mutate().setTint(student.color)
            llDutyCircle.background = background
            
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            todayDutyRecord = db.getDutyRecord(student.id, today)
            
            updateDutyCompletionUI()
        } ?: run {
            tvDutyInitial.text = "?"
            tvDutyName.text = "未分配"
            tvDutyInitial.setTextColor(resources.getColor(R.color.text_secondary))
            val background = resources.getDrawable(R.drawable.circle_border, null)
            background.mutate().setTint(resources.getColor(R.color.text_secondary))
            llDutyCircle.background = background
            llDutyActions.visibility = LinearLayout.GONE
            tvCompletedAt.visibility = LinearLayout.GONE
        }
    }

    private fun updateDutyCompletionUI() {
        todayDutyRecord?.let { record ->
            if (record.completed) {
                tvMarkComplete.text = "已完成"
                btnMarkComplete.isEnabled = false
                btnMarkComplete.alpha = 0.6f
                tvCompletedAt.text = "完成于: ${record.completedAt}"
                tvCompletedAt.visibility = LinearLayout.VISIBLE
                btnLike.isEnabled = true
                btnLike.alpha = 1.0f
            } else {
                tvMarkComplete.text = "标记完成"
                btnMarkComplete.isEnabled = true
                btnMarkComplete.alpha = 1.0f
                tvCompletedAt.visibility = LinearLayout.GONE
                btnLike.isEnabled = false
                btnLike.alpha = 0.5f
            }
            tvLikes.text = record.likes.toString()
            llDutyActions.visibility = LinearLayout.VISIBLE
        } ?: run {
            tvMarkComplete.text = "标记完成"
            btnMarkComplete.isEnabled = true
            btnMarkComplete.alpha = 1.0f
            tvCompletedAt.visibility = LinearLayout.GONE
            tvLikes.text = "0"
            btnLike.isEnabled = false
            btnLike.alpha = 0.5f
            llDutyActions.visibility = LinearLayout.VISIBLE
        }
    }
    
    private fun likeTodayDuty() {
        todayDuty?.let { student ->
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            db.addLike(student.id, today)
            todayDutyRecord = db.getDutyRecord(student.id, today)
            updateDutyCompletionUI()
            Toast.makeText(this, "已点赞！", Toast.LENGTH_SHORT).show()
        }
    }

    private fun markTodayDutyComplete() {
        todayDuty?.let { student ->
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            val now = java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
            db.markDutyCompleted(student.id, today, now)
            todayDutyRecord = db.getDutyRecord(student.id, today)
            updateDutyCompletionUI()
            updateStatistics()
            Toast.makeText(this, "值日已标记为完成！", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateStatistics() {
        val completionRate = db.getCompletionRate()
        tvCompletionRate.text = String.format("%.1f%%", completionRate)

        var totalRating = 0.0
        var ratingCount = 0
        students.forEach { student ->
            val (_, avgRating) = db.getStudentStats(student.id)
            if (avgRating > 0) {
                totalRating += avgRating
                ratingCount++
            }
        }
        val overallAvgRating = if (ratingCount > 0) totalRating / ratingCount else 0.0
        tvAvgRating.text = String.format("%.1f", overallAvgRating)
    }

    private fun showRatingDialog() {
        todayDuty?.let { student ->
            val dialogView = layoutInflater.inflate(R.layout.dialog_rating, null)
            val llStars = dialogView.findViewById<LinearLayout>(R.id.llStars)
            val etReviewNotes = dialogView.findViewById<android.widget.EditText>(R.id.etReviewNotes)
            
            var currentRating = todayDutyRecord?.rating ?: 0
            
            for (i in 1..5) {
                val starView = TextView(this).apply {
                    text = if (i <= currentRating) "★" else "☆"
                    textSize = 32f
                    setTextColor(if (i <= currentRating) resources.getColor(R.color.accent) else resources.getColor(R.color.text_hint))
                    setPadding(8, 0, 8, 0)
                    isClickable = true
                    setOnClickListener {
                        currentRating = i
                        for (j in 0 until llStars.childCount) {
                            val star = llStars.getChildAt(j) as TextView
                            star.text = if (j < i) "★" else "☆"
                            star.setTextColor(if (j < i) resources.getColor(R.color.accent) else resources.getColor(R.color.text_hint))
                        }
                    }
                }
                llStars.addView(starView)
            }
            
            etReviewNotes.setText(todayDutyRecord?.reviewNotes ?: "")
            
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("提交") { _, _ ->
                    val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                    val notes = etReviewNotes.text.toString().ifEmpty { null }
                    db.updateRating(student.id, today, currentRating, notes)
                    todayDutyRecord = db.getDutyRecord(student.id, today)
                    updateStatistics()
                    Toast.makeText(this, "评价已提交！", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("取消", null)
                .show()
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

    private fun updateUpcomingDutiesUI() {
        val items = mutableListOf<UpcomingDutyItem>()
        var currentDate = LocalDate.now().plusDays(1)
        
        for (i in 0 until 7) {
            val dateStr = currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val (dutyStudent, _) = db.getDutyByDate(dateStr)
            items.add(UpcomingDutyItem(currentDate, dutyStudent))
            currentDate = currentDate.plusDays(1)
        }

        val adapter = UpcomingDutyAdapter(items)
        rvUpcomingDuties.adapter = adapter
    }

    private fun showThemeDialog() {
        val themes = arrayOf("跟随系统", "浅色模式", "深色模式")
        val currentTheme = prefs.themeMode
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("选择主题")
            .setSingleChoiceItems(themes, currentTheme) { dialog, which ->
                ThemeManager.setThemeMode(this, which)
                dialog.dismiss()
                ThemeManager.recreateActivity(this)
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun startCountdown() {
        countdownRunnable = object : Runnable {
            override fun run() {
                updateCountdown()
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(countdownRunnable!!)
    }

    private fun stopCountdown() {
        countdownRunnable?.let { handler.removeCallbacks(it) }
    }

    private fun updateCountdown() {
        val reminderHour = prefs.reminderHour
        val reminderMinute = prefs.reminderMinute
        
        val now = LocalDateTime.now()
        val reminderTime = LocalTime.of(reminderHour, reminderMinute)
        var targetDateTime = LocalDateTime.of(LocalDate.now(), reminderTime)
        
        if (targetDateTime.isBefore(now)) {
            targetDateTime = targetDateTime.plusDays(1)
        }
        
        val diffMillis = java.time.Duration.between(now, targetDateTime).toMillis()
        
        if (diffMillis > 0) {
            val hours = TimeUnit.MILLISECONDS.toHours(diffMillis)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis) % 60
            val seconds = TimeUnit.MILLISECONDS.toSeconds(diffMillis) % 60
            
            val countdownText = if (hours > 0) {
                String.format("%02d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format("%02d:%02d", minutes, seconds)
            }
            
            tvCountdown.text = "$countdownText"
            llCountdown.visibility = LinearLayout.VISIBLE
        } else {
            llCountdown.visibility = LinearLayout.GONE
        }
    }
}

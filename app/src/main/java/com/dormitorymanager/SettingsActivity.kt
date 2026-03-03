package com.dormitorymanager

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileWriter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class SettingsActivity : AppCompatActivity() {

    private lateinit var prefs: PreferencesHelper
    private var selectedStartDate: LocalDate? = null
    private var dormitorySize = 6
    private var reminderHour = 8
    private var reminderMinute = 0
    private var reminderAdvanceTime = 0
    private var reminderRepeat = false
    private var reminderVoice = false
    private var splashAnimation = true

    private lateinit var etDormitoryName: EditText
    private lateinit var tvStartDate: TextView
    private lateinit var tvDormitorySize: TextView
    private lateinit var btnDecreaseSize: Button
    private lateinit var btnIncreaseSize: Button
    private lateinit var switchReminder: Switch
    private lateinit var llReminderTime: LinearLayout
    private lateinit var tvReminderTime: TextView
    private lateinit var tvAdvanceTime: TextView
    private lateinit var switchRepeat: Switch
    private lateinit var switchVoice: Switch
    private lateinit var switchSplashAnimation: Switch
    private lateinit var tvManageRoommates: TextView
    private lateinit var tvCurrentUser: TextView
    private lateinit var btnSave: Button
    private lateinit var btnBackup: Button
    private lateinit var btnRestore: Button
    private lateinit var btnSync: Button
    private lateinit var db: DatabaseHelper
    
    private var selectedUserId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        prefs = PreferencesHelper(this)
        db = DatabaseHelper(this)
        initViews()
        loadSettings()
    }

    private fun initViews() {
        etDormitoryName = findViewById(R.id.etDormitoryName)
        tvStartDate = findViewById(R.id.tvStartDate)
        tvDormitorySize = findViewById(R.id.tvDormitorySize)
        btnDecreaseSize = findViewById(R.id.btnDecreaseSize)
        btnIncreaseSize = findViewById(R.id.btnIncreaseSize)
        switchReminder = findViewById(R.id.switchReminder)
        llReminderTime = findViewById(R.id.llReminderTime)
        tvReminderTime = findViewById(R.id.tvReminderTime)
        tvAdvanceTime = findViewById(R.id.tvAdvanceTime)
        switchRepeat = findViewById(R.id.switchRepeat)
        switchVoice = findViewById(R.id.switchVoice)
        switchSplashAnimation = findViewById(R.id.switchSplashAnimation)
        tvManageRoommates = findViewById(R.id.tvManageRoommates)
        tvCurrentUser = findViewById(R.id.tvCurrentUser)
        btnSave = findViewById(R.id.btnSave)
        btnBackup = findViewById(R.id.btnBackup)
        btnRestore = findViewById(R.id.btnRestore)
        btnSync = findViewById(R.id.btnSync)

        btnBackup.setOnClickListener {
            backupData()
        }

        btnRestore.setOnClickListener {
            restoreData()
        }

        btnSync.setOnClickListener {
            startActivity(Intent(this, SyncActivity::class.java))
        }

        findViewById<android.widget.ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }

        tvStartDate.setOnClickListener {
            showDatePicker()
        }

        btnDecreaseSize.setOnClickListener {
            if (dormitorySize > 1) {
                dormitorySize--
                updateSizeDisplay()
            }
        }

        btnIncreaseSize.setOnClickListener {
            if (dormitorySize < 20) {
                dormitorySize++
                updateSizeDisplay()
            }
        }

        switchReminder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                llReminderTime.visibility = android.view.View.VISIBLE
            } else {
                llReminderTime.visibility = android.view.View.GONE
            }
        }

        tvReminderTime.setOnClickListener {
            showTimePicker()
        }

        tvAdvanceTime.setOnClickListener {
            showAdvanceTimePicker()
        }

        tvManageRoommates.setOnClickListener {
            startActivity(Intent(this, ManageStudentsActivity::class.java))
        }

        tvCurrentUser.setOnClickListener {
            showStudentPicker()
        }

        btnSave.setOnClickListener {
            saveSettings()
        }
    }

    private fun loadSettings() {
        etDormitoryName.setText(prefs.dormitoryName)
        dormitorySize = prefs.dormitorySize
        updateSizeDisplay()
        
        prefs.startDate?.let {
            selectedStartDate = LocalDate.parse(it)
            val formatter = DateTimeFormatter.ofPattern("yyyy年M月d日")
            tvStartDate.text = selectedStartDate!!.format(formatter)
        }

        switchReminder.isChecked = prefs.reminderEnabled
        if (prefs.reminderEnabled) {
            llReminderTime.visibility = android.view.View.VISIBLE
        }
        reminderHour = prefs.reminderHour
        reminderMinute = prefs.reminderMinute
        reminderAdvanceTime = prefs.reminderAdvanceTime
        reminderRepeat = prefs.reminderRepeat
        reminderVoice = prefs.reminderVoice
        updateReminderTimeDisplay()
        updateAdvanceTimeDisplay()
        switchRepeat.isChecked = reminderRepeat
        switchVoice.isChecked = reminderVoice
        splashAnimation = prefs.splashAnimation
        switchSplashAnimation.isChecked = splashAnimation
        
        selectedUserId = prefs.currentUserId
        updateCurrentUserDisplay()
    }

    private fun updateSizeDisplay() {
        tvDormitorySize.text = dormitorySize.toString()
    }
    
    private fun updateCurrentUserDisplay() {
        if (selectedUserId == -1L) {
            tvCurrentUser.text = "请选择"
        } else {
            val student = db.getStudentById(selectedUserId)
            tvCurrentUser.text = student?.name ?: "请选择"
        }
    }
    
    private fun showStudentPicker() {
        val students = db.getStudents()
        if (students.isEmpty()) {
            Toast.makeText(this, "请先添加室友", Toast.LENGTH_SHORT).show()
            return
        }
        
        val studentNames = students.map { it.name }.toTypedArray()
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("选择自己")
        builder.setItems(studentNames) { _, which ->
            selectedUserId = students[which].id
            updateCurrentUserDisplay()
        }
        builder.show()
    }

    private fun updateReminderTimeDisplay() {
        tvReminderTime.text = String.format("%02d:%02d", reminderHour, reminderMinute)
    }

    private fun updateAdvanceTimeDisplay() {
        val text = when (reminderAdvanceTime) {
            PreferencesHelper.REMINDER_ADVANCE_NONE -> "准时提醒"
            PreferencesHelper.REMINDER_ADVANCE_15_MIN -> "提前15分钟"
            PreferencesHelper.REMINDER_ADVANCE_30_MIN -> "提前30分钟"
            PreferencesHelper.REMINDER_ADVANCE_1_HOUR -> "提前1小时"
            PreferencesHelper.REMINDER_ADVANCE_2_HOURS -> "提前2小时"
            PreferencesHelper.REMINDER_ADVANCE_1_DAY -> "提前1天"
            else -> "准时提醒"
        }
        tvAdvanceTime.text = text
    }

    private fun showAdvanceTimePicker() {
        val options = arrayOf(
            "准时提醒",
            "提前15分钟",
            "提前30分钟",
            "提前1小时",
            "提前2小时",
            "提前1天"
        )
        val values = intArrayOf(
            PreferencesHelper.REMINDER_ADVANCE_NONE,
            PreferencesHelper.REMINDER_ADVANCE_15_MIN,
            PreferencesHelper.REMINDER_ADVANCE_30_MIN,
            PreferencesHelper.REMINDER_ADVANCE_1_HOUR,
            PreferencesHelper.REMINDER_ADVANCE_2_HOURS,
            PreferencesHelper.REMINDER_ADVANCE_1_DAY
        )
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("选择提前提醒时间")
        builder.setItems(options) { _, which ->
            reminderAdvanceTime = values[which]
            updateAdvanceTimeDisplay()
        }
        builder.show()
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

    private fun showTimePicker() {
        val timePicker = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                reminderHour = hourOfDay
                reminderMinute = minute
                updateReminderTimeDisplay()
            },
            reminderHour,
            reminderMinute,
            true
        )
        timePicker.show()
    }

    private fun saveSettings() {
        val name = etDormitoryName.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(this, "请输入宿舍名称", Toast.LENGTH_SHORT).show()
            return
        }

        prefs.dormitoryName = name
        prefs.dormitorySize = dormitorySize
        
        selectedStartDate?.let {
            prefs.startDate = it.format(DateTimeFormatter.ISO_LOCAL_DATE)
        }

        prefs.reminderEnabled = switchReminder.isChecked
        prefs.reminderHour = reminderHour
        prefs.reminderMinute = reminderMinute
        prefs.reminderAdvanceTime = reminderAdvanceTime
        prefs.reminderRepeat = switchRepeat.isChecked
        prefs.reminderVoice = switchVoice.isChecked
        prefs.splashAnimation = switchSplashAnimation.isChecked
        prefs.currentUserId = selectedUserId

        val alarmHelper = AlarmManagerHelper(this)
        if (switchReminder.isChecked) {
            alarmHelper.setReminder(reminderHour, reminderMinute, reminderAdvanceTime, switchRepeat.isChecked)
        } else {
            alarmHelper.cancelReminder()
        }

        Toast.makeText(this, "保存成功！", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun backupData() {
        try {
            val students = db.getStudents()
            val gson = Gson()
            val data = BackupData(
                dormitoryName = prefs.dormitoryName,
                dormitorySize = prefs.dormitorySize,
                startDate = prefs.startDate,
                reminderEnabled = prefs.reminderEnabled,
                reminderHour = prefs.reminderHour,
                reminderMinute = prefs.reminderMinute,
                reminderAdvanceTime = prefs.reminderAdvanceTime,
                reminderRepeat = prefs.reminderRepeat,
                reminderVoice = prefs.reminderVoice,
                currentUserId = prefs.currentUserId,
                students = students
            )

            val json = gson.toJson(data)
            
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val backupFile = File(downloadsDir, "dormitory_backup_${System.currentTimeMillis()}.json")
            
            FileWriter(backupFile).use { it.write(json) }
            
            Toast.makeText(this, "备份成功！文件已保存到: ${backupFile.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "备份失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun restoreData() {
        Toast.makeText(this, "请选择要恢复的备份文件", Toast.LENGTH_SHORT).show()
    }
}

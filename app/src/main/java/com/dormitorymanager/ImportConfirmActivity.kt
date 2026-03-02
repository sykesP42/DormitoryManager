package com.dormitorymanager

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ImportConfirmActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var prefs: PreferencesHelper
    private lateinit var tvDormName: TextView
    private lateinit var tvDormSize: TextView
    private lateinit var tvStartDate: TextView
    private lateinit var tvReminder: TextView
    private lateinit var tvDutyCount: TextView
    private lateinit var tvRecordCount: TextView
    private lateinit var rvStudents: RecyclerView
    private lateinit var btnCancel: MaterialButton
    private lateinit var btnConfirm: MaterialButton

    private lateinit var backupData: BackupData
    private lateinit var adapter: ImportStudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_import_confirm)

        db = DatabaseHelper(this)
        prefs = PreferencesHelper(this)

        val dataJson = intent.getStringExtra("data_json")
        if (dataJson == null) {
            Toast.makeText(this, "数据无效", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        backupData = Gson().fromJson(dataJson, BackupData::class.java)

        initViews()
        displayData()
    }

    private fun initViews() {
        tvDormName = findViewById(R.id.tvDormName)
        tvDormSize = findViewById(R.id.tvDormSize)
        tvStartDate = findViewById(R.id.tvStartDate)
        tvReminder = findViewById(R.id.tvReminder)
        tvDutyCount = findViewById(R.id.tvDutyCount)
        tvRecordCount = findViewById(R.id.tvRecordCount)
        rvStudents = findViewById(R.id.rvStudents)
        btnCancel = findViewById(R.id.btnCancel)
        btnConfirm = findViewById(R.id.btnConfirm)

        findViewById<android.widget.ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }

        btnCancel.setOnClickListener {
            finish()
        }

        btnConfirm.setOnClickListener {
            importData()
        }

        rvStudents.layoutManager = LinearLayoutManager(this)
        adapter = ImportStudentAdapter(backupData.students)
        rvStudents.adapter = adapter
    }

    private fun displayData() {
        tvDormName.text = "宿舍名称: ${backupData.dormitoryName}"
        tvDormSize.text = "宿舍人数: ${backupData.dormitorySize}人"
        
        if (backupData.startDate != null) {
            val date = LocalDate.parse(backupData.startDate)
            val formatter = DateTimeFormatter.ofPattern("yyyy年M月d日")
            tvStartDate.text = "开始日期: ${date.format(formatter)}"
        } else {
            tvStartDate.text = "开始日期: 未设置"
        }

        if (backupData.reminderEnabled) {
            tvReminder.text = "提醒: 已启用 (${String.format("%02d:%02d", backupData.reminderHour, backupData.reminderMinute)})"
        } else {
            tvReminder.text = "提醒: 未启用"
        }

        tvDutyCount.text = "调休/补值日记录: ${backupData.duties.size}条"
        tvRecordCount.text = "值日记录(完成/评分/点赞): ${backupData.dutyRecords.size}条"
    }

    private fun importData() {
        try {
            prefs.dormitoryName = backupData.dormitoryName
            prefs.dormitorySize = backupData.dormitorySize
            prefs.startDate = backupData.startDate
            prefs.reminderEnabled = backupData.reminderEnabled
            prefs.reminderHour = backupData.reminderHour
            prefs.reminderMinute = backupData.reminderMinute

            db.deleteAllStudents()
            val studentIdMap = mutableMapOf<Long, Long>()
            backupData.students.forEach { student ->
                val newId = db.addStudent(student)
                studentIdMap[student.id] = newId
            }

            val updatedDuties = backupData.duties.map { duty ->
                val newStudentId = studentIdMap[duty.studentId] ?: duty.studentId
                duty.copy(studentId = newStudentId)
            }
            db.replaceAllDuties(updatedDuties)

            val updatedRecords = backupData.dutyRecords.map { record ->
                val newStudentId = studentIdMap[record.studentId] ?: record.studentId
                record.copy(studentId = newStudentId)
            }
            db.replaceAllDutyRecords(updatedRecords)

            if (backupData.reminderEnabled) {
                val alarmHelper = AlarmManagerHelper(this)
                alarmHelper.setReminder(backupData.reminderHour, backupData.reminderMinute)
            }

            Toast.makeText(this, "导入成功！包含排行和所有信息已同步", Toast.LENGTH_SHORT).show()
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "导入失败: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}

class ImportStudentAdapter(private val students: List<Student>) :
    RecyclerView.Adapter<ImportStudentAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val tvAvatar: TextView = itemView.findViewById(R.id.tvAvatar)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvOrder: TextView = itemView.findViewById(R.id.tvOrder)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_import_student, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val student = students[position]
        holder.tvAvatar.text = student.name.firstOrNull()?.toString() ?: "?"
        holder.tvName.text = student.name
        holder.tvOrder.text = "第 ${student.orderIndex + 1} 位"
    }

    override fun getItemCount(): Int = students.size
}

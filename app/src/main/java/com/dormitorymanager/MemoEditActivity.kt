package com.dormitorymanager

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MemoEditActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var btnBack: ImageView
    private lateinit var btnSave: TextView
    private lateinit var llSelectStudent: LinearLayout
    private lateinit var llSelectedStudentAvatar: LinearLayout
    private lateinit var tvSelectedStudentInitial: TextView
    private lateinit var tvSelectedStudentName: TextView
    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText

    private var memoId: Long? = null
    private var selectedStudentId: Long? = null
    private var students: List<Student> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_edit)

        db = DatabaseHelper(this)
        memoId = intent.getLongExtra("memo_id", -1).takeIf { it != -1L }

        initViews()
        loadStudents()
        setupListeners()

        if (memoId != null) {
            loadMemo()
        }
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnSave = findViewById(R.id.btnSave)
        llSelectStudent = findViewById(R.id.llSelectStudent)
        llSelectedStudentAvatar = findViewById(R.id.llSelectedStudentAvatar)
        tvSelectedStudentInitial = findViewById(R.id.tvSelectedStudentInitial)
        tvSelectedStudentName = findViewById(R.id.tvSelectedStudentName)
        etTitle = findViewById(R.id.etTitle)
        etContent = findViewById(R.id.etContent)
    }

    private fun loadStudents() {
        students = db.getStudents()
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            saveMemo()
        }

        llSelectStudent.setOnClickListener {
            showStudentPicker()
        }
    }

    private fun loadMemo() {
        val memo = db.getMemos().find { it.id == memoId } ?: return
        selectedStudentId = memo.studentId
        etTitle.setText(memo.title)
        etContent.setText(memo.content)
        updateSelectedStudentDisplay()
    }

    private fun showStudentPicker() {
        val studentNames = listOf("不关联") + students.map { it.name }
        AlertDialog.Builder(this)
            .setTitle("选择室友")
            .setItems(studentNames.toTypedArray()) { _, which ->
                if (which == 0) {
                    selectedStudentId = null
                } else {
                    selectedStudentId = students[which - 1].id
                }
                updateSelectedStudentDisplay()
            }
            .show()
    }

    private fun updateSelectedStudentDisplay() {
        val student = selectedStudentId?.let { id -> students.find { it.id == id } }
        if (student != null) {
            llSelectedStudentAvatar.visibility = LinearLayout.VISIBLE
            llSelectedStudentAvatar.setBackgroundColor(student.color)
            tvSelectedStudentInitial.text = student.name.takeLast(1)
            tvSelectedStudentName.text = student.name
        } else {
            llSelectedStudentAvatar.visibility = LinearLayout.GONE
            tvSelectedStudentName.text = "不关联"
        }
    }

    private fun saveMemo() {
        val title = etTitle.text.toString().trim()
        if (title.isEmpty()) {
            etTitle.error = "请输入标题"
            return
        }

        val content = etContent.text.toString().trim().takeIf { it.isNotEmpty() }
        val date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

        val memo = Memo(
            id = memoId ?: 0,
            studentId = selectedStudentId,
            title = title,
            content = content,
            date = date
        )

        if (memoId != null) {
            db.updateMemo(memo)
        } else {
            db.addMemo(memo)
        }

        finish()
    }
}

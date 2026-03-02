package com.dormitorymanager

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ManageStudentsActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private val colors = listOf(
        0xFFFF6B6B.toInt(),
        0xFFFFA500.toInt(),
        0xFFFFE66D.toInt(),
        0xFF4ECDC4.toInt(),
        0xFF45B7D1.toInt(),
        0xFF667EEA.toInt(),
        0xFFA855F7.toInt(),
        0xFFEC4899.toInt()
    )
    private lateinit var editStudents: MutableList<EditableStudent>
    private lateinit var adapter: StudentEditAdapter
    private var currentEditPosition = -1

    private lateinit var rvEditStudents: RecyclerView
    private lateinit var btnAddStudent: Button
    private lateinit var btnCancel: Button
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_students)

        db = DatabaseHelper(this)
        initViews()
        loadStudents()
    }

    private fun initViews() {
        rvEditStudents = findViewById(R.id.rvEditStudents)
        btnAddStudent = findViewById(R.id.btnAddStudent)
        btnCancel = findViewById(R.id.btnCancel)
        btnSave = findViewById(R.id.btnSave)

        findViewById<android.widget.ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }

        btnAddStudent.setOnClickListener {
            addNewStudent()
        }

        btnCancel.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            saveStudents()
        }

        rvEditStudents.layoutManager = LinearLayoutManager(this)
    }

    private fun loadStudents() {
        val students = db.getStudents()
        editStudents = students.map {
            EditableStudent(it.id, it.name, it.color, it.orderIndex)
        }.toMutableList()

        adapter = StudentEditAdapter(
            students = editStudents,
            colors = colors,
            onColorClick = { position ->
                currentEditPosition = position
                showColorPicker()
            },
            onDeleteClick = { position ->
                showDeleteConfirm(position)
            }
        )
        rvEditStudents.adapter = adapter
    }

    private fun addNewStudent() {
        val newIndex = editStudents.size
        val color = colors[newIndex % colors.size]
        val newStudent = EditableStudent(
            id = 0L,
            name = "",
            color = color,
            orderIndex = newIndex
        )
        adapter.addStudent(newStudent)
        rvEditStudents.scrollToPosition(editStudents.size - 1)
    }

    private fun showColorPicker() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_color_picker, null)
        val gridColors = dialogView.findViewById<GridLayout>(R.id.gridColors)

        colors.forEachIndexed { index, color ->
            val colorView = layoutInflater.inflate(R.layout.item_color, gridColors, false) as View
            colorView.setBackgroundColor(color)
            colorView.setOnClickListener {
                if (currentEditPosition >= 0) {
                    adapter.updateColor(currentEditPosition, color)
                }
            }
            gridColors.addView(colorView)
        }

        AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
            .show()
    }

    private fun showDeleteConfirm(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("确认删除")
            .setMessage("确定要删除这位室友吗？")
            .setPositiveButton("删除") { _, _ ->
                adapter.removeStudent(position)
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun saveStudents() {
        val currentStudents = adapter.getStudents()
        
        for (student in currentStudents) {
            if (student.name.isBlank()) {
                Toast.makeText(this, "请填写所有室友的姓名", Toast.LENGTH_SHORT).show()
                return
            }
        }

        db.deleteAllStudents()
        
        currentStudents.forEachIndexed { index, student ->
            val newStudent = Student(
                id = 0L,
                name = student.name,
                color = student.color,
                orderIndex = index
            )
            db.insertStudent(newStudent)
        }

        db.initializeDuties()

        Toast.makeText(this, "保存成功！", Toast.LENGTH_SHORT).show()
        finish()
    }
}

package com.dormitorymanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView

data class EditableStudent(
    var id: Long,
    var name: String,
    var color: Int,
    var orderIndex: Int,
    var isDeleted: Boolean = false
)

class StudentEditAdapter(
    private val students: MutableList<EditableStudent>,
    private val colors: List<Int>,
    private val onColorClick: (Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<StudentEditAdapter.StudentEditViewHolder>() {

    inner class StudentEditViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llColorPicker: LinearLayout = itemView.findViewById(R.id.llColorPicker)
        val etName: EditText = itemView.findViewById(R.id.etStudentName)
        val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)

        fun bind(position: Int, student: EditableStudent) {
            llColorPicker.setBackgroundColor(student.color)
            etName.setText(student.name)
            
            etName.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    student.name = etName.text.toString().trim()
                }
            }

            llColorPicker.setOnClickListener {
                onColorClick(position)
            }

            ivDelete.setOnClickListener {
                onDeleteClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentEditViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_edit, parent, false)
        return StudentEditViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentEditViewHolder, position: Int) {
        holder.bind(position, students[position])
    }

    override fun getItemCount(): Int = students.size

    fun getStudents(): List<EditableStudent> = students.filter { !it.isDeleted }

    fun addStudent(student: EditableStudent) {
        students.add(student)
        notifyItemInserted(students.size - 1)
    }

    fun removeStudent(position: Int) {
        if (students[position].id == 0L) {
            students.removeAt(position)
            notifyItemRemoved(position)
        } else {
            students[position].isDeleted = true
            notifyItemChanged(position)
        }
    }

    fun updateColor(position: Int, color: Int) {
        students[position].color = color
        notifyItemChanged(position)
    }
}

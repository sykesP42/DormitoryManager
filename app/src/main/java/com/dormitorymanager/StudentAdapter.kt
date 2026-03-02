package com.dormitorymanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView

class StudentAdapter(
    private val students: List<Student>,
    private val todayDutyId: Long? = null,
    private val onItemClick: (Student) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llAvatar: LinearLayout = itemView.findViewById(R.id.llStudentAvatar)
        val tvInitial: TextView = itemView.findViewById(R.id.tvStudentInitial)
        val tvName: TextView = itemView.findViewById(R.id.tvStudentName)
        val tvTodayDuty: TextView = itemView.findViewById(R.id.tvTodayDuty)

        fun bind(student: Student) {
            tvInitial.text = student.name.takeLast(1)
            tvName.text = student.name
            llAvatar.setBackgroundColor(student.color)
            
            if (student.id == todayDutyId) {
                tvTodayDuty.visibility = View.VISIBLE
            } else {
                tvTodayDuty.visibility = View.GONE
            }

            itemView.setOnClickListener { onItemClick(student) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(students[position])
    }

    override fun getItemCount(): Int = students.size
}

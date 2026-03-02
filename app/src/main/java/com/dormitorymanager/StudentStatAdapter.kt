package com.dormitorymanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class StudentStatItem(
    val student: Student,
    val dutyCount: Int
)

class StudentStatAdapter(
    private val items: List<StudentStatItem>
) : RecyclerView.Adapter<StudentStatAdapter.StudentStatViewHolder>() {

    inner class StudentStatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llStudentAvatar: LinearLayout = itemView.findViewById(R.id.llStudentAvatar)
        val tvStudentInitial: TextView = itemView.findViewById(R.id.tvStudentInitial)
        val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
        val tvDutyCount: TextView = itemView.findViewById(R.id.tvDutyCount)

        fun bind(item: StudentStatItem) {
            tvStudentInitial.text = item.student.name.takeLast(1)
            tvStudentName.text = item.student.name
            llStudentAvatar.setBackgroundColor(item.student.color)
            tvDutyCount.text = "值日 ${item.dutyCount} 次"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentStatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_stat, parent, false)
        return StudentStatViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentStatViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}

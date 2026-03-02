package com.dormitorymanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class DutyHistoryItem(
    val date: LocalDate,
    val student: Student?,
    val type: String,
    val notes: String?
)

class DutyHistoryAdapter(
    private val items: List<DutyHistoryItem>
) : RecyclerView.Adapter<DutyHistoryAdapter.DutyHistoryViewHolder>() {

    inner class DutyHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llItemBackground: LinearLayout = itemView.findViewById(R.id.llItemBackground)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
        val tvType: TextView = itemView.findViewById(R.id.tvType)
        val tvNotes: TextView = itemView.findViewById(R.id.tvNotes)

        fun bind(item: DutyHistoryItem) {
            val formatter = DateTimeFormatter.ofPattern("yyyy年M月d日")
            tvDate.text = item.date.format(formatter)
            
            item.student?.let {
                tvStudentName.text = it.name
                llItemBackground.setBackgroundColor(it.color)
            } ?: run {
                tvStudentName.text = "未分配"
            }

            when (item.type) {
                "normal" -> tvType.text = "正常值日"
                "timeoff" -> tvType.text = "调休"
                "makeup" -> tvType.text = "补值日"
                "swap" -> tvType.text = "换班"
                else -> tvType.text = item.type
            }

            if (item.notes.isNullOrBlank()) {
                tvNotes.visibility = View.GONE
            } else {
                tvNotes.visibility = View.VISIBLE
                tvNotes.text = item.notes
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DutyHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_duty_history, parent, false)
        return DutyHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DutyHistoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}

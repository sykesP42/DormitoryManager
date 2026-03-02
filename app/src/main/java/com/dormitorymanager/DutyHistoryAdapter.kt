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
    val notes: String?,
    val completed: Boolean = false,
    val completedAt: String? = null,
    val rating: Int = 0,
    val likes: Int = 0
)

class DutyHistoryAdapter(
    private val items: List<DutyHistoryItem>
) : RecyclerView.Adapter<DutyHistoryAdapter.DutyHistoryViewHolder>() {

    inner class DutyHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llItemBackground: LinearLayout = itemView.findViewById(R.id.llItemBackground)
        val cardView: androidx.cardview.widget.CardView = itemView as androidx.cardview.widget.CardView
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
        val tvType: TextView = itemView.findViewById(R.id.tvType)
        val tvNotes: TextView = itemView.findViewById(R.id.tvNotes)
        val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        val tvLikes: TextView = itemView.findViewById(R.id.tvLikes)
        val tvCompletedAt: TextView = itemView.findViewById(R.id.tvCompletedAt)

        fun bind(item: DutyHistoryItem) {
            val formatter = DateTimeFormatter.ofPattern("yyyy年M月d日")
            tvDate.text = item.date.format(formatter)
            
            item.student?.let {
                tvStudentName.text = it.name
                cardView.setCardBackgroundColor(it.color)
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

            tvRating.text = if (item.rating > 0) item.rating.toString() else "-"
            tvLikes.text = item.likes.toString()
            
            if (item.completed && item.completedAt != null) {
                tvCompletedAt.text = "完成于 ${item.completedAt}"
                tvCompletedAt.visibility = View.VISIBLE
            } else {
                tvCompletedAt.visibility = View.GONE
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

package com.dormitorymanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class UpcomingDutyItem(
    val date: LocalDate,
    val student: Student?
)

class UpcomingDutyAdapter(
    private val items: List<UpcomingDutyItem>
) : RecyclerView.Adapter<UpcomingDutyAdapter.UpcomingDutyViewHolder>() {

    inner class UpcomingDutyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llDutyAvatar: LinearLayout = itemView.findViewById(R.id.llDutyAvatar)
        val tvDutyInitial: TextView = itemView.findViewById(R.id.tvDutyInitial)
        val tvDutyDate: TextView = itemView.findViewById(R.id.tvDutyDate)
        val tvDutyName: TextView = itemView.findViewById(R.id.tvDutyName)

        fun bind(item: UpcomingDutyItem) {
            val formatter = DateTimeFormatter.ofPattern("M月d日 EEEE")
            tvDutyDate.text = item.date.format(formatter)

            item.student?.let {
                tvDutyInitial.text = it.name.takeLast(1)
                tvDutyName.text = it.name
                llDutyAvatar.setBackgroundColor(it.color)
            } ?: run {
                tvDutyInitial.text = "?"
                tvDutyName.text = "未分配"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingDutyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_upcoming_duty, parent, false)
        return UpcomingDutyViewHolder(view)
    }

    override fun onBindViewHolder(holder: UpcomingDutyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}

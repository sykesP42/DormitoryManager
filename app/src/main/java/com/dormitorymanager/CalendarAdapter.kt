package com.dormitorymanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate

data class CalendarDayItem(
    val date: LocalDate?,
    val isCurrentMonth: Boolean,
    val dutyType: String? = null,
    val student: Student? = null
)

class CalendarAdapter(
    private val items: List<CalendarDayItem>,
    private val onDayClick: (LocalDate) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarDayViewHolder>() {

    inner class CalendarDayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDayNumber: TextView = itemView.findViewById(R.id.tvDayNumber)
        val vDutyIndicator: View = itemView.findViewById(R.id.vDutyIndicator)

        fun bind(item: CalendarDayItem) {
            item.date?.let { date ->
                tvDayNumber.text = date.dayOfMonth.toString()

                if (item.isCurrentMonth) {
                    tvDayNumber.alpha = 1f
                } else {
                    tvDayNumber.alpha = 0.3f
                }

                val today = LocalDate.now()
                if (date.isEqual(today)) {
                    tvDayNumber.setBackgroundResource(R.drawable.circle_background)
                    tvDayNumber.background.mutate().setTint(tvDayNumber.resources.getColor(R.color.primary))
                    tvDayNumber.setTextColor(tvDayNumber.resources.getColor(R.color.white))
                } else {
                    tvDayNumber.setBackgroundResource(0)
                    tvDayNumber.setTextColor(tvDayNumber.resources.getColor(R.color.text_primary))
                }

                item.dutyType?.let { type ->
                    vDutyIndicator.visibility = View.VISIBLE
                    val color = when (type) {
                        "timeoff" -> R.color.accent
                        "makeup" -> R.color.color_4
                        "swap" -> R.color.color_5
                        else -> R.color.primary
                    }
                    vDutyIndicator.setBackgroundColor(vDutyIndicator.resources.getColor(color))
                } ?: run {
                    vDutyIndicator.visibility = View.INVISIBLE
                }

                itemView.setOnClickListener {
                    onDayClick(date)
                }
            } ?: run {
                tvDayNumber.text = ""
                vDutyIndicator.visibility = View.INVISIBLE
                itemView.setOnClickListener(null)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarDayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return CalendarDayViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarDayViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}

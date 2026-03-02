package com.dormitorymanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class LeaderboardItem(
    val rank: Int,
    val student: Student,
    val dutyCount: Int
)

class LeaderboardAdapter(
    private val items: List<LeaderboardItem>
) : RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

    inner class LeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRank: TextView = itemView.findViewById(R.id.tvRank)
        val llAvatar: LinearLayout = itemView.findViewById(R.id.llAvatar)
        val tvAvatar: TextView = itemView.findViewById(R.id.tvAvatar)
        val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
        val tvDutyCount: TextView = itemView.findViewById(R.id.tvDutyCount)
        val tvScore: TextView = itemView.findViewById(R.id.tvScore)

        fun bind(item: LeaderboardItem) {
            when (item.rank) {
                1 -> {
                    tvRank.text = "🥇"
                    tvRank.textSize = 28f
                }
                2 -> {
                    tvRank.text = "🥈"
                    tvRank.textSize = 26f
                }
                3 -> {
                    tvRank.text = "🥉"
                    tvRank.textSize = 24f
                }
                else -> {
                    tvRank.text = item.rank.toString()
                    tvRank.textSize = 20f
                }
            }

            tvAvatar.text = item.student.name.takeLast(1)
            llAvatar.setBackgroundColor(item.student.color)
            tvStudentName.text = item.student.name
            tvDutyCount.text = "${item.dutyCount} 次值日"
            tvScore.text = item.dutyCount.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard, parent, false)
        return LeaderboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}

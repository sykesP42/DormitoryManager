package com.dormitorymanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MemoAdapter(
    private val memos: List<MemoWithStudent>,
    private val onItemClick: (Memo) -> Unit,
    private val onItemLongClick: (Memo) -> Unit
) : RecyclerView.Adapter<MemoAdapter.MemoViewHolder>() {

    inner class MemoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llStudentAvatar: LinearLayout = itemView.findViewById(R.id.llStudentAvatar)
        val tvStudentInitial: TextView = itemView.findViewById(R.id.tvStudentInitial)
        val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvContent: TextView = itemView.findViewById(R.id.tvContent)

        fun bind(memoWithStudent: MemoWithStudent) {
            val memo = memoWithStudent.memo
            val student = memoWithStudent.student

            if (student != null) {
                llStudentAvatar.visibility = View.VISIBLE
                llStudentAvatar.setBackgroundColor(student.color)
                tvStudentInitial.text = student.name.takeLast(1)
                tvStudentName.text = student.name
                tvStudentName.visibility = View.VISIBLE
            } else {
                llStudentAvatar.visibility = View.GONE
                tvStudentName.visibility = View.GONE
            }

            val date = LocalDate.parse(memo.date)
            tvDate.text = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            tvTitle.text = memo.title
            tvContent.text = memo.content ?: ""

            itemView.setOnClickListener { onItemClick(memo) }
            itemView.setOnLongClickListener {
                onItemLongClick(memo)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_memo, parent, false)
        return MemoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        holder.bind(memos[position])
    }

    override fun getItemCount(): Int = memos.size
}

data class MemoWithStudent(
    val memo: Memo,
    val student: Student?
)

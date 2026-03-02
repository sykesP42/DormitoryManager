package com.dormitorymanager

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.random.Random

class RandomPickerActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var btnBack: ImageView
    private lateinit var btnDecrease: ImageView
    private lateinit var btnIncrease: ImageView
    private lateinit var tvCount: TextView
    private lateinit var tvResultHint: TextView
    private lateinit var rvResults: RecyclerView
    private lateinit var btnPick: Button

    private var pickCount = 1
    private var students = emptyList<Student>()
    private var isRolling = false
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var resultAdapter: PickedStudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random_picker)

        db = DatabaseHelper(this)
        initViews()
        loadStudents()
        setupListeners()
        setupRecyclerView()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnDecrease = findViewById(R.id.btnDecrease)
        btnIncrease = findViewById(R.id.btnIncrease)
        tvCount = findViewById(R.id.tvCount)
        tvResultHint = findViewById(R.id.tvResultHint)
        rvResults = findViewById(R.id.rvResults)
        btnPick = findViewById(R.id.btnPick)
    }

    private fun loadStudents() {
        students = db.getStudents()
        updatePickButtonState()
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnDecrease.setOnClickListener {
            if (pickCount > 1) {
                pickCount--
                updateCountDisplay()
            }
        }

        btnIncrease.setOnClickListener {
            if (pickCount < students.size) {
                pickCount++
                updateCountDisplay()
            }
        }

        btnPick.setOnClickListener {
            if (isRolling) {
                stopRolling()
            } else {
                startRolling()
            }
        }
    }

    private fun setupRecyclerView() {
        rvResults.layoutManager = LinearLayoutManager(this)
        resultAdapter = PickedStudentAdapter(emptyList())
        rvResults.adapter = resultAdapter
    }

    private fun updateCountDisplay() {
        tvCount.text = pickCount.toString()
    }

    private fun updatePickButtonState() {
        btnPick.isEnabled = students.isNotEmpty()
        if (students.isEmpty()) {
            tvResultHint.text = "没有室友可抽取"
        }
    }

    private fun startRolling() {
        isRolling = true
        btnPick.text = "停止"
        tvResultHint.visibility = View.GONE
        rvResults.visibility = View.VISIBLE

        val rollRunnable = object : Runnable {
            override fun run() {
                if (isRolling) {
                    val randomStudents = getRandomStudents(pickCount)
                    resultAdapter.updateData(randomStudents)
                    handler.postDelayed(this, 100)
                }
            }
        }
        handler.post(rollRunnable)
    }

    private fun stopRolling() {
        isRolling = false
        handler.removeCallbacksAndMessages(null)
        btnPick.text = "重新抽取"

        val finalStudents = getRandomStudents(pickCount)
        resultAdapter.updateData(finalStudents)
    }

    private fun getRandomStudents(count: Int): List<Student> {
        return students.shuffled(Random(System.currentTimeMillis())).take(count)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}

class PickedStudentAdapter(
    private var students: List<Student>
) : RecyclerView.Adapter<PickedStudentAdapter.StudentViewHolder>() {

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llAvatar: LinearLayout = itemView.findViewById(R.id.llAvatar)
        val tvInitial: TextView = itemView.findViewById(R.id.tvInitial)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_picked_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.llAvatar.setBackgroundColor(student.color)
        holder.tvInitial.text = student.name.takeLast(1)
        holder.tvName.text = student.name
    }

    override fun getItemCount(): Int = students.size

    fun updateData(newStudents: List<Student>) {
        students = newStudents
        notifyDataSetChanged()
    }
}

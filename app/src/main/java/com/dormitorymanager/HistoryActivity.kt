package com.dormitorymanager

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistoryActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var rvHistory: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: DutyHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        db = DatabaseHelper(this)
        initViews()
        loadHistory()
    }

    private fun initViews() {
        rvHistory = findViewById(R.id.rvHistory)
        tvEmpty = findViewById(R.id.tvEmpty)

        findViewById<android.widget.ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }

        rvHistory.layoutManager = LinearLayoutManager(this)
    }

    private fun loadHistory() {
        val historyItems = db.getDutyHistory()

        if (historyItems.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            rvHistory.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            rvHistory.visibility = View.VISIBLE

            adapter = DutyHistoryAdapter(historyItems)
            rvHistory.adapter = adapter
        }
    }
}

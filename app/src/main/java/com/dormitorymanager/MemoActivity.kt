package com.dormitorymanager

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MemoActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var btnBack: ImageView
    private lateinit var btnAdd: ImageView
    private lateinit var rvMemos: RecyclerView
    private lateinit var adapter: MemoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo)

        db = DatabaseHelper(this)
        initViews()
        setupListeners()
        loadMemos()
    }

    override fun onResume() {
        super.onResume()
        loadMemos()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnAdd = findViewById(R.id.btnAdd)
        rvMemos = findViewById(R.id.rvMemos)
        rvMemos.layoutManager = LinearLayoutManager(this)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnAdd.setOnClickListener {
            startActivity(Intent(this, MemoEditActivity::class.java))
        }
    }

    private fun loadMemos() {
        val memos = db.getMemos()
        val students = db.getStudents().associateBy { it.id }
        val memosWithStudents = memos.map { memo ->
            MemoWithStudent(memo, memo.studentId?.let { students[it] })
        }

        adapter = MemoAdapter(
            memosWithStudents,
            onItemClick = { memo ->
                val intent = Intent(this, MemoEditActivity::class.java).apply {
                    putExtra("memo_id", memo.id)
                }
                startActivity(intent)
            },
            onItemLongClick = { memo ->
                showDeleteDialog(memo)
            }
        )
        rvMemos.adapter = adapter
    }

    private fun showDeleteDialog(memo: Memo) {
        AlertDialog.Builder(this)
            .setTitle("删除备忘录")
            .setMessage("确定要删除这条备忘录吗？")
            .setPositiveButton("删除") { _, _ ->
                db.deleteMemo(memo.id)
                loadMemos()
            }
            .setNegativeButton("取消", null)
            .show()
    }
}

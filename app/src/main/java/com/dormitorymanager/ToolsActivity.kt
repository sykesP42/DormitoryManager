package com.dormitorymanager

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class ToolsActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var cardMemo: CardView
    private lateinit var cardRandomPicker: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tools)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        cardMemo = findViewById(R.id.cardMemo)
        cardRandomPicker = findViewById(R.id.cardRandomPicker)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        cardMemo.setOnClickListener {
            startActivity(Intent(this, MemoActivity::class.java))
        }

        cardRandomPicker.setOnClickListener {
            startActivity(Intent(this, RandomPickerActivity::class.java))
        }
    }
}

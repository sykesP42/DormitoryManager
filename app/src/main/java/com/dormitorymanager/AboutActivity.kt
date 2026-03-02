package com.dormitorymanager

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {

    private val GITHUB_URL = "https://github.com/sykesP42/DormitoryManager"
    private val RELEASES_URL = "https://github.com/sykesP42/DormitoryManager/releases"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        findViewById<android.widget.ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.cardGithub).setOnClickListener {
            openUrl(GITHUB_URL)
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.cardReleases).setOnClickListener {
            openUrl(RELEASES_URL)
        }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}

package com.ivar7284.rbi_pay

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton

class ProfileActivity : AppCompatActivity() {

    private lateinit var backBtn: ImageButton
    private lateinit var logoutBtn: CircularProgressButton

    private val URL = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        logoutBtn = findViewById(R.id.logoutBtn)
        logoutBtn.setOnClickListener {
            logoutBtn.startAnimation()
            //deleteAccessToken()
        }

        // Back button
        backBtn = findViewById(R.id.ib_backBtn)
        backBtn.setOnClickListener {
            finish()
        }
    }
}
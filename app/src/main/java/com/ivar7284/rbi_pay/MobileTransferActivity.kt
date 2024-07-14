package com.ivar7284.rbi_pay

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

class MobileTransferActivity : AppCompatActivity() {

    private lateinit var backBtn: ImageView
    private lateinit var mobileSearch: EditText
    private lateinit var subHeading: TextView // for visibility
    private lateinit var searchResult: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mobile_transfer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //search and showing using recyclerview
        mobileSearch = findViewById(R.id.search_bar_et)
        subHeading = findViewById(R.id.subHeading) //make visible
        searchResult = findViewById(R.id.mobile_search_rv) // make visible

        //back button
        backBtn = findViewById(R.id.back_btn)
        backBtn.setOnClickListener {
            finish()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


}
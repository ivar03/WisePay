package com.ivar7284.rbi_pay

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class BankTransferActivity : AppCompatActivity() {

    private lateinit var backBtn: ImageView
    private lateinit var accountNo: EditText
    private lateinit var ifscCode: EditText
    private lateinit var continueBtn: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bank_transfer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //process info to search database
        accountNo = findViewById(R.id.account_number_et)
        ifscCode = findViewById(R.id.ifsc_code_et)
        continueBtn = findViewById(R.id.continue_button)
        continueBtn.setOnClickListener {
            //
            Toast.makeText(this, "Not yet implemented", Toast.LENGTH_SHORT).show()
        }

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
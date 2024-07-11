package com.ivar7284.rbi_pay

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton

class PaymentActivity : AppCompatActivity() {

    private lateinit var upi_tv : TextView
    private lateinit var upi_username_tv : TextView
    private lateinit var back_btn : ImageView
    private lateinit var amount_et : EditText
    private lateinit var pay_btn : CircularProgressButton
    private lateinit var cancel_btn : CircularProgressButton

    private var username : String? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //amount
        amount_et = findViewById(R.id.amount_et)
        amount_et.requestFocus()
        val amt = amount_et.text.toString() // stored amount to variable for transmitting to server

        //pay btn
        pay_btn = findViewById(R.id.pay_button)
        pay_btn.setOnClickListener {
            // TODO: opening the upi portal for money transfer
            //for now send amt to transfer, device model details, location to the server
        }

        //info from the qr scanner
        val qrResult = intent.getStringExtra("QR_RESULT")
        upi_tv = findViewById(R.id.detail2_tv)
        upi_tv.text = "UPI ID $qrResult"

        //get username from the database by sending the upi id
        upi_username_tv = findViewById(R.id.detail1_tv)
        //fetchUsername(qrResult)
        upi_username_tv.text = "Paying ${username}"

        //cancel btn
        cancel_btn = findViewById(R.id.cancel_button)
        cancel_btn.setOnClickListener {
            finish()
        }

        //back btn
        back_btn = findViewById(R.id.back_btn)
        back_btn.setOnClickListener {
            finish()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
package com.ivar7284.rbi_pay

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton

class VirtualCreditActivity : AppCompatActivity() {

    private lateinit var backBtn: ImageView
    private lateinit var card_no_ll: LinearLayout //for visibility
    private lateinit var card_cvv_ll: LinearLayout //for visibility
    private lateinit var cardNo: TextView
    private lateinit var cardCvv: TextView
    private lateinit var generateBtn: CircularProgressButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_virtual_credit)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //generate button (main logic)
        card_no_ll = findViewById(R.id.card_number_ll)
        card_cvv_ll = findViewById(R.id.card_cvv_ll)
        cardNo = findViewById(R.id.card_number_tv)
        cardCvv = findViewById(R.id.card_cvv_tv)
        generateBtn = findViewById(R.id.generate_button)
        generateBtn.setOnClickListener {
            //backend call and updating the visibility and text of text views
            generateBtn.startAnimation()
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
package com.ivar7284.rbi_pay

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class VirtualDebitActivity : AppCompatActivity() {

    private lateinit var backBtn: ImageView
    private lateinit var generateBtn: CircularProgressButton

    private val URL = "https://rbihackathon2024-production.up.railway.app/accounts/generate-random-debit-card/"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_virtual_debit)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //generate button (main logic)
        generateBtn = findViewById(R.id.generate_button)
        generateBtn.setOnClickListener {
            //backend call and updating the visibility and text of text views
            generateBtn.startAnimation()
            fetchData()
        }

        //back button
        backBtn = findViewById(R.id.back_btn)
        backBtn.setOnClickListener {
            finish()
        }

    }

    private fun fetchData() {
        val accessToken = getAccessToken()
        if (accessToken.isNullOrEmpty()) {
            Log.e("fetchData", "Access token is null or empty")
            return
        }

        val requestQueue = Volley.newRequestQueue(this)
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, URL, null,
            { response ->
                val cardNo = response.getString("card_number")
                val cvv = response.getString("cvv")
                val pin = response.getString("pin")

                findViewById<LinearLayout>(R.id.card_number_ll).visibility = View.VISIBLE
                findViewById<LinearLayout>(R.id.card_pin_ll).visibility = View.VISIBLE
                findViewById<LinearLayout>(R.id.card_cvv_ll).visibility = View.VISIBLE

                findViewById<TextView>(R.id.card_number_tv).text = cardNo
                findViewById<TextView>(R.id.card_cvv_tv).text = cvv
                findViewById<TextView>(R.id.card_pin_tv).text = pin

                generateBtn.revertAnimation()
            },
            { error ->
                Log.i("error fetching", error.message.toString())
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $accessToken"
                return headers
            }
        }
        requestQueue.add(jsonObjectRequest)
    }

    private fun getAccessToken(): String? {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("access_token", null)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
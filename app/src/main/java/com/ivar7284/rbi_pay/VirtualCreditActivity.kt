package com.ivar7284.rbi_pay

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

class VirtualCreditActivity : AppCompatActivity() {

    private lateinit var backBtn: ImageView
    private lateinit var generateBtn: CircularProgressButton

    private val URL = "https://web-production-99b4c.up.railway.app/accounts/generate-random-credit-card/"

    @SuppressLint("MissingInflatedId")
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
        generateBtn = findViewById(R.id.generate_button)
        generateBtn.setOnClickListener {
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
            Toast.makeText(this, "Authentication error: Access token is missing.", Toast.LENGTH_SHORT).show()
            generateBtn.revertAnimation()
            return
        }

        val requestQueue = Volley.newRequestQueue(this)
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, URL, null,
            { response ->
                try {
                    val cardNo = response.getString("card_number")
                    val cvv = response.getString("cvv")
                    val pin = response.getString("pin")

                    findViewById<LinearLayout>(R.id.card_number_ll).visibility = View.VISIBLE
                    findViewById<LinearLayout>(R.id.card_pin_ll).visibility = View.VISIBLE
                    findViewById<LinearLayout>(R.id.card_cvv_ll).visibility = View.VISIBLE

                    findViewById<TextView>(R.id.card_number_tv).text = cardNo
                    findViewById<TextView>(R.id.card_cvv_tv).text = cvv
                    findViewById<TextView>(R.id.card_pin_tv).text = pin

                } catch (e: JSONException) {
                    Log.e("JSONError", "Error parsing response: ${e.message}")
                    Toast.makeText(this, "Error processing data. Please try again.", Toast.LENGTH_SHORT).show()
                }
                generateBtn.revertAnimation()
            },
            { error ->
                generateBtn.revertAnimation()

                val statusCode = error.networkResponse?.statusCode
                val data = error.networkResponse?.data
                val errorMessage = data?.let { String(it) } ?: "Unknown error"

                Log.e("Volley Error", "Status Code: $statusCode, Response: $errorMessage")

                when (statusCode) {
                    500 -> Toast.makeText(this, "Internal server error. Please try again later.", Toast.LENGTH_SHORT).show()
                    401 -> Toast.makeText(this, "Unauthorized. Please check your access token.", Toast.LENGTH_SHORT).show()
                    404 -> Toast.makeText(this, "Resource not found.", Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show()
                }
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
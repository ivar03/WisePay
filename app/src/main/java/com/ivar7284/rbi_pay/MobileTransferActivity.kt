package com.ivar7284.rbi_pay

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class MobileTransferActivity : AppCompatActivity() {

    private lateinit var backBtn: ImageView
    private lateinit var mobileSearch: EditText
    private lateinit var subHeading: TextView // for visibility
    private lateinit var searchResult: RecyclerView

    private val URL = "https://rbihackathon2024-production.up.railway.app/user-detail/"

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

        //fetch user detail
        val accessToken = getAccessToken()

        if (accessToken.isNullOrEmpty()) {
            Log.e("fetchData", "Access token is null or empty")
            return
        }

        val requestQueue = Volley.newRequestQueue(this)
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, URL, null,
            { response ->
                val email = response.getString("email")
                val phone = response.getString("number")
                val upiId = response.getString("upi_id")

                Log.i("upi id", upiId)
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

    private fun getAccessToken(): String? {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("access_token", null)
    }

}
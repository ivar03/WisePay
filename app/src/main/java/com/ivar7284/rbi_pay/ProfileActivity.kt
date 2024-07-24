package com.ivar7284.rbi_pay

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class ProfileActivity : AppCompatActivity() {

    private lateinit var backBtn: ImageButton
    private lateinit var logoutBtn: CircularProgressButton

    private val URL = "https://web-production-99b4c.up.railway.app/user/user-details/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
                val phone = response.getString("phn")
                val name = response.getString("name")

                findViewById<TextView>(R.id.Pemail).text = "Email: $email"
                findViewById<TextView>(R.id.Pphone).text = "Mobile Number: $phone"
                findViewById<TextView>(R.id.PName).text = name
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

        //logout button
        logoutBtn = findViewById(R.id.logoutBtn)
        logoutBtn.setOnClickListener {
            logoutBtn.startAnimation()
            deleteAccessToken()
            startActivity(Intent(applicationContext, LoginRegisterActivity::class.java))
            finish()
            logoutBtn.revertAnimation()
        }

        // Back button
        backBtn = findViewById(R.id.ib_backBtn)
        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun deleteAccessToken() {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("access_token")
        editor.apply()
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
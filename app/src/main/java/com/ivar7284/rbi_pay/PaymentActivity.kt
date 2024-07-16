package com.ivar7284.rbi_pay

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject

class PaymentActivity : AppCompatActivity() {

    private lateinit var upi_tv : TextView
    private lateinit var upi_username_tv : TextView
    private lateinit var back_btn : ImageView
    private lateinit var amount_et : EditText
    private lateinit var pay_btn : CircularProgressButton
    private lateinit var cancel_btn : CircularProgressButton

    private var username : String? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sharedPreferences: SharedPreferences

    private val URL = "https://rbihackathon2024-production.up.railway.app/payment/"

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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //amount
        amount_et = findViewById(R.id.amount_et)
        amount_et.requestFocus()

        //info from the qr scanner
        val qrResult = intent.getStringExtra("QR_RESULT")
        upi_tv = findViewById(R.id.detail2_tv)
        upi_tv.text = "UPI ID $qrResult"

        //search for user's name using the upi id from the database using url - user/AllUserDetail
        fetchData()


        //pay btn
        pay_btn = findViewById(R.id.pay_button)
        pay_btn.setOnClickListener {
            val amt = amount_et.text.toString()
            Toast.makeText(this,"$amt", Toast.LENGTH_SHORT).show()
            if (amt.isNotEmpty()) {
                logPaymentDetails(amt)
                paymentReq(amt, qrResult.toString())
            } else {
                amount_et.error = "Please enter an amount to send!"
            }
        }

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
                //Todo: response se upi id pr search krke user ka naam nikalna aur return krna
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

    private fun paymentReq(amt: String, qrResult: String) {
        val deviceModel = Build.MODEL
        var latitude: String? = null
        var longitude: String? = null
        var locationStr: String? = null
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let {
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()
                        locationStr = "$latitude,$longitude"
                    }
                }
        } else {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show()
        }

        //requesting server
        val req = JSONObject()
        req.put("device_name", deviceModel)
        req.put("location", locationStr)
        req.put("amount", amt)
        req.put("receiver_upi_id", qrResult)
        //req.put("sender_upi_id", upiID)

        val accessToken = sharedPreferences.getString("access_token", null)
        if (accessToken.isNullOrEmpty()) {
            Log.e("fetchData", "Access token is null or empty")
            return
        }

        val requestQueue = Volley.newRequestQueue(applicationContext)
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, URL, req,
            { response ->
                Log.i("response payment", response.toString())
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun logPaymentDetails(amount: String) {
        // Fetch device model
        val deviceModel = Build.MODEL

        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        val locationStr = "Lat: $latitude, Long: $longitude"
                        Log.i("transaction info","Payment Details - Amount: $amount, Device Model: $deviceModel, Location: $locationStr")
                    }
                }
        } else {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show()
        }
    }
}
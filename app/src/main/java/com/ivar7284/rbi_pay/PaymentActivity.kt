package com.ivar7284.rbi_pay

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.Executor

class PaymentActivity : AppCompatActivity() {

    private lateinit var upi_tv: TextView
    private lateinit var upi_username_tv: TextView
    private lateinit var back_btn: ImageView
    private lateinit var amount_et: EditText
    private lateinit var pay_btn: CircularProgressButton
    private lateinit var cancel_btn: CircularProgressButton

    private var username: String? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private val URL = "https://rbihackathon2024-production.up.railway.app/accounts/perform-transaction/"

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
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Initialize BiometricPrompt
        val executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = createBiometricPrompt(this, executor)
        promptInfo = createPromptInfo()

        //amount
        amount_et = findViewById(R.id.amount_et)
        amount_et.requestFocus()

        //info from the qr scanner
        val qrResult = intent.getStringExtra("QR_RESULT")
        upi_tv = findViewById(R.id.detail2_tv)
        upi_tv.text = "UPI ID $qrResult"

        //search for user's name using the upi id from the database using url - user/AllUserDetail/
        fetchData(qrResult)

        //pay btn
        pay_btn = findViewById(R.id.pay_button)
        pay_btn.setOnClickListener {
            val amt = amount_et.text.toString()
            Toast.makeText(this, "$amt", Toast.LENGTH_SHORT).show()
            if (amt.isNotEmpty()) {
                logPaymentDetails(amt)
                biometricPrompt.authenticate(promptInfo)
            } else {
                amount_et.error = "Please enter an amount to send!"
            }
        }

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

    private fun handleUserResponse(response: JSONArray, qrResult: String?) {
        for (i in 0 until response.length()) {
            val user = response.getJSONObject(i)
            val upiId = user.getString("upi_id")
            if (upiId == qrResult) {
                username = user.getString("name")
                break
            }
        }
        upi_username_tv = findViewById(R.id.detail1_tv)
        upi_username_tv.text = "Paying $username"
    }

    private fun fetchData(qrResult: String?) {
        val url = "https://rbihackathon2024-production.up.railway.app/user/AllUserDetail/"
        val accessToken = getAccessToken()

        if (accessToken.isNullOrEmpty()) {
            Log.e("fetchData", "Access token is null or empty")
            return
        }

        val requestQueue = Volley.newRequestQueue(this)
        val jsonArrayRequest = object : JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                handleUserResponse(response, qrResult)
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
        requestQueue.add(jsonArrayRequest)
    }

    private fun getAccessToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    private fun createBiometricPrompt(context: Context, executor: Executor): BiometricPrompt {
        return BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Log.e("BiometricPrompt", "Authentication error: $errorCode - $errString")
                    runOnUiThread {
                        Toast.makeText(
                            this@PaymentActivity,
                            "Authentication error: $errString",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Log.i("BiometricPrompt", "Authentication succeeded")
                    runOnUiThread {
                        Toast.makeText(
                            this@PaymentActivity,
                            "Authentication succeeded",
                            Toast.LENGTH_SHORT
                        ).show()
                        // payment request
                        paymentReq(amount_et.text.toString(), upi_tv.text.toString().replace("UPI ID ", ""))
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Log.e("BiometricPrompt", "Authentication failed")
                    startActivity(Intent(applicationContext, HomeActivity::class.java))
                    finish()
                    runOnUiThread {
                        Toast.makeText(
                            this@PaymentActivity,
                            "Authentication failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
    }

    private fun paymentReq(amt: String, upi: String) {
        val deviceModel = Build.MODEL

        // Request fine location permission if not granted
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            // Fetch location
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let {
                        val latitude = location.latitude.toString()
                        val longitude = location.longitude.toString()
                        val locationStr = "$latitude,$longitude"

                        // Build JSON request
                        val req = JSONObject()
                        req.put("device_name", deviceModel)
                        req.put("location", locationStr)
                        req.put("amount", amt.toFloat())
                        req.put("sender_upi", getUPI())
                        req.put("receiver_upi_id", upi)

                        Log.i("request for payment", req.toString())

                        val accessToken = getAccessToken()
                        // Check if access token is valid
                        if (accessToken.isNullOrEmpty()) {
                            Log.e("fetchData", "Access token is null or empty")
                            return@addOnSuccessListener
                        }

                        Log.i("access token", accessToken)

                        val requestQueue = Volley.newRequestQueue(applicationContext)
                        val jsonObjectRequest = object : JsonObjectRequest(
                            Request.Method.POST, URL, req,
                            { response ->
                                Log.i("response payment", response.toString())
                                showAlertBoxAndStartActivity()
                            },
                            { error ->
                                Log.e("error fetching", error.message ?: "Unknown error")
                                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                            }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Authorization"] = "Bearer $accessToken"
                                headers["Content-Type"] = "application/json" // Ensure correct content type
                                return headers
                            }
                        }
                        requestQueue.add(jsonObjectRequest)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Location", "Failed to get location: ${e.message}")
                    Toast.makeText(this, "Failed to get location: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show()
        }
    }


    private fun showAlertBoxAndStartActivity() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Payment Confirmation")
            .setMessage("Your payment has been successfully completed.")
            .setPositiveButton("Ok") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                Toast.makeText(this, "Payment Successful!!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(applicationContext, HomeActivity::class.java))
                finish()
            }
            .show()
    }


    private fun getUPI(): String? {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("upi_id", null)
    }

    private fun createPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authenticate to proceed with payment")
            .setSubtitle("Verify your fingerprint")
            .setNegativeButtonText("Cancel")
            .build()
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
                        Log.i(
                            "transaction info",
                            "Payment Details - Amount: $amount, Device Model: $deviceModel, Location: $locationStr"
                        )
                    }
                }
        } else {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}

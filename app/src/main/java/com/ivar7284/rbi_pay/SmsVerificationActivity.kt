package com.ivar7284.rbi_pay

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.Telephony
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ivar7284.rbi_pay.adapters.SMSAdapter
import com.ivar7284.rbi_pay.dataclasses.SMSMessage
import org.json.JSONObject

class SmsVerificationActivity : AppCompatActivity() {

    private lateinit var backBtn: ImageView
    private lateinit var sms_rv: RecyclerView
    private lateinit var requestQueue: RequestQueue
    private val messages = mutableListOf<SMSMessage>()

    companion object {
        private const val REQUEST_SMS_PERMISSION = 1
        private const val TAG = "SmsVerificationActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sms_verification)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sms_rv = findViewById(R.id.sms_rv)
        sms_rv.layoutManager = LinearLayoutManager(this)

        requestQueue = Volley.newRequestQueue(this)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS), REQUEST_SMS_PERMISSION)
        } else {
            loadSmsMessages()
        }

        backBtn = findViewById(R.id.back_btn)
        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun loadSmsMessages() {
        val cursor: Cursor? = contentResolver.query(Telephony.Sms.Inbox.CONTENT_URI, null, null, null, Telephony.Sms.DEFAULT_SORT_ORDER)
        cursor?.let {
            val indexBody = cursor.getColumnIndex(Telephony.Sms.BODY)
            val indexAddress = cursor.getColumnIndex(Telephony.Sms.ADDRESS)
            var count = 0
            while (cursor.moveToNext() && count < 50) {
                val message = cursor.getString(indexBody)
                val sender = cursor.getString(indexAddress)
                val smsMessage = SMSMessage(sender, message)
                messages.add(smsMessage)
                sendSpamPredictionRequest(smsMessage)
                count++
            }
            cursor.close()
        }

        val adapter = SMSAdapter(messages)
        sms_rv.adapter = adapter
    }

    private fun sendSpamPredictionRequest(smsMessage: SMSMessage) {
        val url = "https://rbihackathon2024-production.up.railway.app/SpamPred/predict_spam/"
        val requestBody = JSONObject().apply {
            put("text", smsMessage.message)
        }

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, requestBody,
            { response ->
                try {
                    val prediction = response.getInt("prediction")
                    smsMessage.prediction = prediction
                    sms_rv.adapter?.notifyDataSetChanged()
                    Log.d(TAG, "Message: ${smsMessage.message} - Prediction: $prediction")
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing response: ${e.message}")
                }
            },
            { error ->
                Log.e(TAG, "Request error: ${error.message}")
            }
        )

        requestQueue.add(jsonObjectRequest)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_SMS_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadSmsMessages()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}

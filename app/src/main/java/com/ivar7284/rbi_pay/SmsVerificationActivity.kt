package com.ivar7284.rbi_pay

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.Telephony
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ivar7284.rbi_pay.adapters.SMSAdapter
import com.ivar7284.rbi_pay.dataclasses.SMSMessage

class SmsVerificationActivity : AppCompatActivity() {

    private lateinit var backBtn: ImageView
    private lateinit var sms_rv: RecyclerView

    companion object {
        private const val REQUEST_SMS_PERMISSION = 1
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
        val messages = mutableListOf<SMSMessage>()
        val cursor: Cursor? = contentResolver.query(Telephony.Sms.Inbox.CONTENT_URI, null, null, null, Telephony.Sms.DEFAULT_SORT_ORDER)
        cursor?.let {
            val indexBody = cursor.getColumnIndex(Telephony.Sms.BODY)
            val indexAddress = cursor.getColumnIndex(Telephony.Sms.ADDRESS)
            var count = 0
            while (cursor.moveToNext() && count < 50) {
                val message = cursor.getString(indexBody)
                val sender = cursor.getString(indexAddress)
                messages.add(SMSMessage(sender, message))
                count++
            }
            cursor.close()
        }

        val adapter = SMSAdapter(messages)
        sms_rv.adapter = adapter
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

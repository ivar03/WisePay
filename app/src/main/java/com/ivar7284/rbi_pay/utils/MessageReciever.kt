package com.ivar7284.rbi_pay.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log

class MessageReceiver : BroadcastReceiver() {

    private var message: String? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (intent != null && intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                for (smsMessage in smsMessages) {
                    message = smsMessage.messageBody
                }
            } else {
                val bundle = intent.extras
                if (bundle != null) {
                    val pdus = bundle["pdus"] as Array<Any>?
                    if (pdus != null) {
                        val smsMessage = SmsMessage.createFromPdu(pdus[0] as ByteArray)
                        message = smsMessage.messageBody
                    }
                }
            }
            Log.i("messages", message.toString())
        }
    }
}
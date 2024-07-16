package com.ivar7284.rbi_pay.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ivar7284.rbi_pay.R
import com.ivar7284.rbi_pay.dataclasses.SMSMessage

class SMSAdapter(private val messages: List<SMSMessage>) : RecyclerView.Adapter<SMSAdapter.SMSViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SMSViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sms_itemview, parent, false)
        return SMSViewHolder(view)
    }

    override fun onBindViewHolder(holder: SMSViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount() = messages.size

    class SMSViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val senderTextView: TextView = view.findViewById(R.id.sms_sender)
        private val messageTextView: TextView = view.findViewById(R.id.sms_content)
        private val riskIcon: View = view.findViewById(R.id.risk_icon)
        private val mainRl: RelativeLayout = view.findViewById(R.id.main_rl)

        fun bind(smsMessage: SMSMessage) {
            senderTextView.text = smsMessage.sender
            val fullMessage = smsMessage.message
            val shortMessage = if (fullMessage.length > 50) fullMessage.substring(0, 50) + "..." else fullMessage
            messageTextView.text = shortMessage

            if (smsMessage.prediction == 1) {
                riskIcon.visibility = View.VISIBLE
                mainRl.setBackgroundColor(Color.parseColor("#33740112"))
            } else {
                riskIcon.visibility = View.GONE
                mainRl.setBackgroundColor(Color.TRANSPARENT)
            }

            itemView.setOnClickListener {
                if (messageTextView.text == shortMessage) {
                    messageTextView.text = fullMessage
                } else {
                    messageTextView.text = shortMessage
                }
            }
        }
    }
}

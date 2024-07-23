package com.ivar7284.rbi_pay.adapters

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ivar7284.rbi_pay.R
import com.ivar7284.rbi_pay.ReportActivity
import com.ivar7284.rbi_pay.dataclasses.Transaction
import org.json.JSONObject

class TransactionAdapter(private val transactions: List<Transaction>) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_itemview, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.bind(transaction)
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val receiverTextView: TextView = itemView.findViewById(R.id.transaction_receiver)
        private val amountTextView: TextView = itemView.findViewById(R.id.transaction_amt)
        private val dateTextView: TextView = itemView.findViewById(R.id.transaction_date)
        private val transactionId: TextView = itemView.findViewById(R.id.transaction_id)
        private val customerLoc: TextView = itemView.findViewById(R.id.customer_location)
        private val customerAcc: TextView = itemView.findViewById(R.id.customer_account_balance)
        private val transactionTime: TextView = itemView.findViewById(R.id.transaction_time)
        private val confirmationBtn: TextView = itemView.findViewById(R.id.confirmation_btn)
        private val ReportBtn: TextView = itemView.findViewById(R.id.report_btn)

        init {
            // Initially hide the additional details
            transactionId.visibility = View.GONE
            customerLoc.visibility = View.GONE
            customerAcc.visibility = View.GONE
            transactionTime.visibility = View.GONE
            confirmationBtn.visibility = View.GONE
            ReportBtn.visibility = View.GONE

            // Set an onClickListener to toggle visibility
            itemView.setOnClickListener {
                val visibility = if (transactionId.visibility == View.GONE) View.VISIBLE else View.GONE
                transactionId.visibility = visibility
                customerLoc.visibility = visibility
                customerAcc.visibility = visibility
                transactionTime.visibility = visibility
                confirmationBtn.visibility = visibility
                ReportBtn.visibility = visibility
            }

            confirmationBtn.setOnClickListener {
                showAlertDialog(itemView.context)
            }

            ReportBtn.setOnClickListener {
                showAlertDialogForReport(itemView.context)
            }
        }

        fun bind(transaction: Transaction) {
            receiverTextView.text = transaction.receiver_phno
            amountTextView.text = "Rs.${transaction.transaction_amount}"
            dateTextView.text = transaction.transaction_date
            transactionId.text = "Transaction ID: ${transaction.transaction_id}"
            customerLoc.text = "Customer Location: ${transaction.customer_location}"
            customerAcc.text = "Account Balance: ${transaction.customer_account_balance}"
            transactionTime.text = "Transaction Time: ${transaction.transaction_time}"
        }

        private fun showAlertDialog(context: Context) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Transaction Confirmation")
                .setMessage("Do you want to confirm this transaction?")
                .setPositiveButton("Yes") { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    Toast.makeText(context, "Transaction has been confirmed!", Toast.LENGTH_SHORT).show()
                    sendConfirmation(context)
                    confirmationBtn.text = "Confirmed"
                    confirmationBtn.isClickable = false
                }
                .setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                }
                .show()
        }
        private fun showAlertDialogForReport(context: Context) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Report")
                .setMessage("Do you want to report this transaction?")
                .setPositiveButton("Yes") { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    Toast.makeText(context, "Opening report screen!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, ReportActivity::class.java)
                    intent.putExtra("transaction_id", transactionId.text.toString().substringAfter("Transaction ID: "))
                    context.startActivity(intent)
                }
                .setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                }
                .show()
        }

        private fun sendConfirmation(context: Context) {
            val url = "" // Your URL here
            val accessToken = getAccessToken(context)
            if (accessToken.isNullOrEmpty()) {
                Log.e("fetchData", "Access token is null or empty")
                return
            }

            val req = JSONObject()
            req.put("confirmation", 1)

            val requestQueue = Volley.newRequestQueue(context)
            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST, url, req,
                { response ->
                    Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show()
                },
                { error ->
                    Log.i("error fetching", error.message.toString())
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer $accessToken"
                    Log.i("header", headers.toString())
                    return headers
                }
            }
            requestQueue.add(jsonObjectRequest)
        }

        private fun getAccessToken(context: Context): String? {
            val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            return sharedPreferences.getString("access_token", null)
        }
    }
}

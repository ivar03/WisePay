package com.ivar7284.rbi_pay.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ivar7284.rbi_pay.R
import com.ivar7284.rbi_pay.dataclasses.Transaction

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

        init {
            // Initially hide the additional details
            transactionId.visibility = View.GONE
            customerLoc.visibility = View.GONE
            customerAcc.visibility = View.GONE
            transactionTime.visibility = View.GONE

            // Set an onClickListener to toggle visibility
            itemView.setOnClickListener {
                val visibility = if (transactionId.visibility == View.GONE) View.VISIBLE else View.GONE
                transactionId.visibility = visibility
                customerLoc.visibility = visibility
                customerAcc.visibility = visibility
                transactionTime.visibility = visibility
            }
        }

        fun bind(transaction: Transaction) {
            receiverTextView.text = transaction.receiver_phno.toString()
            amountTextView.text = "Rs.${transaction.TransactionAmount}"
            dateTextView.text = transaction.TransactionDate
            transactionId.text = "Transaction ID: ${transaction.TransactionID}"
            customerLoc.text = "Customer Location: ${transaction.CustLocation}"
            customerAcc.text = "Account Balance: ${transaction.CustAccountBalance}"
            transactionTime.text = "Transaction Time: ${transaction.TransactionTime}"
        }
    }
}

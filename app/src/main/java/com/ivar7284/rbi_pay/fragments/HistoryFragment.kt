package com.ivar7284.rbi_pay.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.ivar7284.rbi_pay.R
import com.ivar7284.rbi_pay.adapters.TransactionAdapter
import com.ivar7284.rbi_pay.dataclasses.Transaction
import org.json.JSONArray
import org.json.JSONObject

class HistoryFragment : Fragment() {

    private lateinit var transactionHistoryRV: RecyclerView
    private val URL = "https://rbihackathon2024-production.up.railway.app/accounts/user-transactions/"

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val views = inflater.inflate(R.layout.fragment_history, container, false)

        // Initialize recycler view
        transactionHistoryRV = views.findViewById(R.id.transaction_history_rv)
        transactionHistoryRV.layoutManager = LinearLayoutManager(context)

        getTransactions()

        return views
    }

    private fun getTransactions() {
        val accessToken = getAccessToken()
        if (accessToken.isNullOrEmpty()) {
            Log.e("fetchData", "Access token is null or empty")
            return
        }

        val requestQueue = Volley.newRequestQueue(requireContext())
        val jsonArrayRequest = object : JsonArrayRequest(
            Request.Method.GET, URL, null,
            { response ->
                val transactions = parseTransactions(response)
                transactionHistoryRV.adapter = TransactionAdapter(transactions)
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
        requestQueue.add(jsonArrayRequest)
    }

    private fun parseTransactions(jsonArray: JSONArray): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.optJSONObject(i) ?: continue

            val transaction = Transaction(
                id = jsonObject.optInt("id", -1),
                TransactionID = jsonObject.optString("TransactionID", ""),
                sender_phnno = jsonObject.optLong("sender_phnno", -1),
                receiver_phno = jsonObject.optLong("receiver_phno", -1),
                CustomerID = jsonObject.optString("CustomerID", ""),
                CustomerDOB = jsonObject.optString("CustomerDOB", ""),
                CustGender = jsonObject.optString("CustGender", ""),
                CustLocation = jsonObject.optString("CustLocation", ""),
                CustAccountBalance = jsonObject.optDouble("CustAccountBalance", 0.0),
                TransactionDate = jsonObject.optString("TransactionDate", ""),
                TransactionTime = jsonObject.optLong("TransactionTime", -1),
                TransactionAmount = jsonObject.optDouble("TransactionAmount", 0.0)
            )
            transactions.add(transaction)
        }
        return transactions
    }

    private fun getAccessToken(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("access_token", null)
    }
}

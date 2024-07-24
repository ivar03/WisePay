package com.ivar7284.rbi_pay.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ivar7284.rbi_pay.R
import org.json.JSONException
import org.json.JSONObject

class LockFragment : Fragment() {

    private lateinit var creditCardCheckBox: CheckBox
    private lateinit var debitCardCheckBox: CheckBox
    private lateinit var netBankingCheckBox: CheckBox
    private lateinit var upiCheckBox: CheckBox

    private lateinit var confirmationBtn: CircularProgressButton
    private lateinit var sharedPreferences: SharedPreferences

    private val URL = "https://web-production-99b4c.up.railway.app/accounts/update-lock-status/"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val views = inflater.inflate(R.layout.fragment_lock, container, false)

        creditCardCheckBox = views.findViewById(R.id.credit_card_checkbox)
        debitCardCheckBox = views.findViewById(R.id.debit_card_checkbox)
        netBankingCheckBox = views.findViewById(R.id.internet_banking_checkbox)
        upiCheckBox = views.findViewById(R.id.upi_checkbox)

        // Load checkbox states from SharedPreferences or response data
        loadCheckboxStates()

        //sending data button
        confirmationBtn = views.findViewById(R.id.confirmation_btn)
        confirmationBtn.setOnClickListener {
            confirmationBtn.startAnimation()
            val creditBool = creditCardCheckBox.isChecked
            val debitBool = debitCardCheckBox.isChecked
            val netBankingBool = netBankingCheckBox.isChecked
            val upiBool = upiCheckBox.isChecked

            //send data
            sendData(creditBool, debitBool, netBankingBool, upiBool)
        }

        return views
    }

    private fun loadCheckboxStates() {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        creditCardCheckBox.isChecked = sharedPreferences.getBoolean("credit_checked", false)
        debitCardCheckBox.isChecked = sharedPreferences.getBoolean("debit_checked", false)
        netBankingCheckBox.isChecked = sharedPreferences.getBoolean("net_banking_checked", false)
        upiCheckBox.isChecked = sharedPreferences.getBoolean("upi_checked", false)
    }

    private fun saveCheckboxStates(credit: Boolean, debit: Boolean, netBanking: Boolean, upi: Boolean) {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("credit_checked", credit)
            putBoolean("debit_checked", debit)
            putBoolean("net_banking_checked", netBanking)
            putBoolean("upi_checked", upi)
            apply()
        }
    }

    private fun sendData(credit: Boolean, debit: Boolean, netBanking: Boolean, upi: Boolean) {
        val req = JSONObject()
        req.put("credit", credit)
        req.put("debit", debit)
        req.put("net_banking", netBanking)
        req.put("upi", upi)

        Log.d("SendData Request", req.toString()) // Logging the request JSON

        val accessToken = getAccessToken()
        Log.i("accesstoken", accessToken.toString())

        val requestQueue = Volley.newRequestQueue(requireContext())
        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST, URL, req,
            { response ->
                try {
                    Log.d("SendData Response", response.toString()) // Logging the response JSON

                    // Update checkboxes based on response
                    creditCardCheckBox.isChecked = response.getBoolean("credit")
                    debitCardCheckBox.isChecked = response.getBoolean("debit")
                    netBankingCheckBox.isChecked = response.getBoolean("net_banking")
                    upiCheckBox.isChecked = response.getBoolean("upi")

                    // Save checkbox states
                    saveCheckboxStates(
                        response.getBoolean("credit"),
                        response.getBoolean("debit"),
                        response.getBoolean("net_banking"),
                        response.getBoolean("upi")
                    )

                    confirmationBtn.revertAnimation()
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.e("JSON Error", e.message.toString())
                    confirmationBtn.revertAnimation()
                }
            },
            { error ->
                Log.e("Volley Error", error.message.toString())
                Toast.makeText(requireContext(), "Server error!", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $accessToken"
                return headers
            }
        }

        requestQueue.add(jsonObjectRequest)
    }

    private fun getAccessToken(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("access_token", null)
    }
}
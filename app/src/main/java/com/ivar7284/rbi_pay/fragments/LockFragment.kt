package com.ivar7284.rbi_pay.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
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

    private val URL = "https://rbihackathon2024-production.up.railway.app/locking/"

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val views = inflater.inflate(R.layout.fragment_lock, container, false)

        creditCardCheckBox = views.findViewById(R.id.credit_card_checkbox)
        debitCardCheckBox = views.findViewById(R.id.debit_card_checkbox)
        netBankingCheckBox = views.findViewById(R.id.internet_banking_checkbox)
        upiCheckBox = views.findViewById(R.id.upi_checkbox)

        //sending data button
        confirmationBtn = views.findViewById(R.id.confirmation_btn)
        confirmationBtn.setOnClickListener {
            confirmationBtn.startAnimation()
            val creditBool = creditCardCheckBox.isChecked
            val debitBool = debitCardCheckBox.isChecked
            val netBankingBool = netBankingCheckBox.isChecked
            val upiBool = upiCheckBox.isChecked

            Log.i("bool values", "Credit Card: $creditBool")
            Log.i("bool values", "Debit Card: $debitBool")
            Log.i("bool values", "Net Banking: $netBankingBool")
            Log.i("bool values", "UPI: $upiBool")

            //send data
            sendData(creditBool,debitBool,netBankingBool,upiBool)

            confirmationBtn.revertAnimation()
        }

        return views
    }

    private fun sendData(credit: Boolean, debit: Boolean, netBanking: Boolean, upi: Boolean) {
        val req = JSONObject()
        req.put("credit", credit)
        req.put("debit", debit)
        req.put("net_banking", netBanking)
        req.put("upi", upi)

        Log.i("request json", req.toString())

        val requestQueue = Volley.newRequestQueue(requireContext())
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, URL, req,
            { response ->
                try {
                    val message = response.getString("message")
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.e("JSON Error", e.message.toString())
                }
            },
            { error ->
                Log.e("Volley Error", error.message.toString())
                Toast.makeText(requireContext(), "server error!", Toast.LENGTH_SHORT).show()
            })

        requestQueue.add(jsonObjectRequest)
    }
}
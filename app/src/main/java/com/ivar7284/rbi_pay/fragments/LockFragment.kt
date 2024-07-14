package com.ivar7284.rbi_pay.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.ivar7284.rbi_pay.R

class LockFragment : Fragment() {

    private lateinit var creditCardCheckBox: CheckBox
    private lateinit var debitCardCheckBox: CheckBox
    private lateinit var netBankingCheckBox: CheckBox
    private lateinit var upiCheckBox: CheckBox
    private lateinit var bankingAppCheckBox: CheckBox

    private lateinit var confirmationBtn: CircularProgressButton

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
        bankingAppCheckBox = views.findViewById(R.id.banking_app_checkbox)

        //sending data button
        confirmationBtn = views.findViewById(R.id.confirmation_btn)
        confirmationBtn.setOnClickListener {
            confirmationBtn.startAnimation()
            val creditBool = creditCardCheckBox.isChecked
            val debitBool = debitCardCheckBox.isChecked
            val netBankingBool = netBankingCheckBox.isChecked
            val upiBool = upiCheckBox.isChecked
            val bankAppBool = bankingAppCheckBox.isChecked

            Log.i("bool values", "Credit Card: $creditBool")
            Log.i("bool values", "Debit Card: $debitBool")
            Log.i("bool values", "Net Banking: $netBankingBool")
            Log.i("bool values", "UPI: $upiBool")
            Log.i("bool values", "Banking App: $bankAppBool")

            //send data

            confirmationBtn.revertAnimation()
        }


        return views
    }
}
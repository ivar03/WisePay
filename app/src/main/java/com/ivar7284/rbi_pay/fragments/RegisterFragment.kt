package com.ivar7284.rbi_pay.fragments

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.ivar7284.rbi_pay.HomeActivity
import com.ivar7284.rbi_pay.R

class RegisterFragment : Fragment() {

    private lateinit var name: EditText
    private lateinit var phone: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var cpassword: EditText
    private lateinit var loginBtn: TextView
    private lateinit var registerBtn: CircularProgressButton

    private val URL = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val views = inflater.inflate(R.layout.fragment_register, container, false)

        name = views.findViewById(R.id.et_rname)
        phone = views.findViewById(R.id.et_rphone)
        email = views.findViewById(R.id.et_rmail)
        password = views.findViewById(R.id.et_rpass)
        cpassword = views.findViewById(R.id.et_rconfirmpass)
        loginBtn = views.findViewById(R.id.rlogin_btn)
        registerBtn = views.findViewById(R.id.rregister_btn)

        registerBtn.setOnClickListener {
            registerBtn.startAnimation()
            startActivity(Intent(requireContext(), HomeActivity::class.java))
            requireActivity().finish()
            //registerUser()
        }

        loginBtn.setOnClickListener {
            val loginFrag = LoginFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.login_register_frame, loginFrag)
            fragmentTransaction.commit()
        }


        return views
    }

    private fun showRequiredAlert() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Password match error")
            .setMessage("Confirm Password do not match")
            .setPositiveButton("Ok") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
            .show()
    }

}
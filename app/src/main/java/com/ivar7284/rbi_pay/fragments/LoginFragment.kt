package com.ivar7284.rbi_pay.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.ivar7284.rbi_pay.HomeActivity
import com.ivar7284.rbi_pay.R

class LoginFragment : Fragment() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginBtn: CircularProgressButton
    private lateinit var registerBtn: TextView

    val URL = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val views = inflater.inflate(R.layout.fragment_login, container, false)

        email = views.findViewById(R.id.et_mail)
        password = views.findViewById(R.id.et_pass)
        loginBtn = views.findViewById(R.id.login_btn)
        registerBtn = views.findViewById(R.id.register_btn)

        registerBtn.setOnClickListener {
            val registerFrag = RegisterFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.login_register_frame, registerFrag)
            fragmentTransaction.commit()
        }

        loginBtn.setOnClickListener {
            loginBtn.startAnimation()
            startActivity(Intent(requireContext(), HomeActivity::class.java))
            requireActivity().finish()
            //loginUser()
        }

        return views
    }
}
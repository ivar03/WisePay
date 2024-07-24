package com.ivar7284.rbi_pay.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ivar7284.rbi_pay.HomeActivity
import com.ivar7284.rbi_pay.R
import org.json.JSONException
import org.json.JSONObject

class RegisterFragment : Fragment() {

    private lateinit var name: EditText
    private lateinit var phone: EditText
    private lateinit var email: EditText
    private lateinit var upiId: EditText
    private lateinit var password: EditText
    private lateinit var cpassword: EditText
    private lateinit var loginBtn: TextView
    private lateinit var registerBtn: CircularProgressButton

    private val URL = "https://web-production-99b4c.up.railway.app/user/register/"

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val views = inflater.inflate(R.layout.fragment_register, container, false)

        name = views.findViewById(R.id.et_rname)
        phone = views.findViewById(R.id.et_rphone)
        email = views.findViewById(R.id.et_rmail)
        upiId = views.findViewById(R.id.et_rupi)
        password = views.findViewById(R.id.et_rpass)
        cpassword = views.findViewById(R.id.et_rconfirmpass)
        loginBtn = views.findViewById(R.id.rlogin_btn)
        registerBtn = views.findViewById(R.id.rregister_btn)

        registerBtn.setOnClickListener {
            registerBtn.startAnimation()
            registerUser()
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

    private fun registerUser() {
        val fname: String = name.text.toString()
        val mob: String = phone.text.toString()
        val mail: String = email.text.toString()
        val upiId: String = upiId.text.toString()
        val pass: String = password.text.toString()
        val cpass: String = cpassword.text.toString()

        if (pass == cpass) {
            val req = JSONObject().apply {
                put("name", fname)
                put("email", mail)
                put("upi_id", upiId)
                put("password", pass)
                put("password2", pass)
                put("phn", mob)
            }

            val requestQueue = Volley.newRequestQueue(requireContext())
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, URL, req,
                { response ->
                    try {
                        registerBtn.revertAnimation()
                        Log.d("Server Response", response.toString())
                        autoLogin(mail, pass)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Log.e("JSON Error", e.message.toString())
                    }
                },
                { error ->
                    registerBtn.revertAnimation()

                    val statusCode = error.networkResponse?.statusCode
                    val data = error.networkResponse?.data
                    val errorMessage = data?.let { String(it) } ?: "Unknown error"

                    Log.e("Volley Error", "Status Code: $statusCode, Response: $errorMessage")

                    try {
                        val errorObj = JSONObject(errorMessage)
                        val errorMessages = mutableListOf<String>()

                        errorObj.keys().forEach { key ->
                            val messages = errorObj.getJSONArray(key)
                            for (i in 0 until messages.length()) {
                                errorMessages.add("${key.capitalize()}: ${messages[i]}")
                            }
                        }

                        Toast.makeText(requireContext(), errorMessages.joinToString("\n"), Toast.LENGTH_LONG).show()
                    } catch (e: JSONException) {
                        // In case of JSON parsing error, log it and show a generic message
                        e.printStackTrace()
                        Toast.makeText(requireContext(), "Registration failed!", Toast.LENGTH_SHORT).show()
                    }
                })

            requestQueue.add(jsonObjectRequest)

        } else {
            showRequiredAlert()
        }
    }

    private fun autoLogin(mail:String, pass:String) {
        val requestQueue = Volley.newRequestQueue(requireContext())

        val url = "https://web-production-99b4c.up.railway.app/user/login/"

        val reqLogin = JSONObject()
        reqLogin.put("email", mail)
        reqLogin.put("password", pass)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, reqLogin,
            { response ->
                try {
                    val token = response.getString("access")
                    Log.i("accessToken", token)
                    saveAccessToken(token)
                    Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), HomeActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.e("JSON Error", e.message.toString())
                }
            },
            { error ->
                Log.e("Volley Error", error.message.toString())
                Toast.makeText(requireContext(), "Login failed!", Toast.LENGTH_SHORT).show()
            })

        requestQueue.add(jsonObjectRequest)
    }

    private fun saveAccessToken(token: String) {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("access_token", token)
            apply()
        }
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
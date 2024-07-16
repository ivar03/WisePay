package com.ivar7284.rbi_pay

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Timer
import java.util.TimerTask

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.splash_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Check if biometric authentication is available
        val biometricManager = BiometricManager.from(this)
        if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
            // biometric prompt
            val biometricPrompt = createBiometricPrompt()
            val promptInfo = createBiometricPromptInfo()

            // authenticate user
            biometricPrompt.authenticate(promptInfo)
        } else {
            navigateToMainActivity()
        }
    }

    private fun createBiometricPrompt(): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(this)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                showFailureToastAndCloseApp()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                navigateToMainActivity()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                showRetryDialog()
            }
        }
        return BiometricPrompt(this, executor, callback)
    }

    private fun createBiometricPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Verification")
            .setSubtitle("Log in using your biometric credential")
            .setDeviceCredentialAllowed(true) // allow fallback to device PIN/password
            .build()
    }

    private fun showRetryDialog() {
        runOnUiThread {
            AlertDialog.Builder(this)
                .setTitle("Authentication Failed")
                .setMessage("Biometric authentication failed. Would you like to try again?")
                .setPositiveButton("Retry") { _, _ ->
                    val biometricPrompt = createBiometricPrompt()
                    val promptInfo = createBiometricPromptInfo()
                    biometricPrompt.authenticate(promptInfo)
                }
                .setNegativeButton("Cancel") { _, _ ->
                    showFailureToastAndCloseApp()
                }
                .show()
        }
    }

    private fun showFailureToastAndCloseApp() {
        runOnUiThread {
            Toast.makeText(this, "App can't be opened without authentication", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun navigateToMainActivity() {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                startActivity(Intent(applicationContext, LoginRegisterActivity::class.java))
                finish()
            }
        }, 1000)
    }
}

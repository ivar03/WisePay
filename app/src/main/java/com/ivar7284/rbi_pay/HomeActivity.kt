package com.ivar7284.rbi_pay

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.ivar7284.rbi_pay.fragments.HistoryFragment
import com.ivar7284.rbi_pay.fragments.HomeFragment
import com.ivar7284.rbi_pay.fragments.LockFragment
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var drawerButton: ImageButton

    private lateinit var homeBtn: LinearLayout
    private lateinit var profileBtn: LinearLayout
    private lateinit var lockerBtn: LinearLayout
    private lateinit var historyBtn: LinearLayout
    private lateinit var scannerBtn: ImageView

    private val SMS_PERMISSION_REQUEST_CODE = 200

    private lateinit var sharedPreferences: SharedPreferences

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                showCamera()
            } else {
                // Handle the case when the permission is not granted
                Toast.makeText(this, "Camera Permission required", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    private val scanLauncher =
        registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
            run {
                if (result.contents == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
                } else {
                    setResult(result.contents)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        overridePendingTransition(0, 0)

        //permission for sms reading
        requestPermissions(arrayOf(android.Manifest.permission.RECEIVE_SMS), SMS_PERMISSION_REQUEST_CODE)

        loadFragment(HomeFragment())
        findViewById<View>(R.id.home_view).backgroundTintList =
            ColorStateList.valueOf(Color.parseColor("#03534d"))
        findViewById<TextView>(R.id.home_tv).visibility = View.VISIBLE

        // Initialize views and set click listeners
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        drawerButton = findViewById(R.id.drawer_button)

        drawerButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_qr -> {
                    //
                }

                R.id.nav_locker -> {
                    val lockerFrag = LockFragment()
                    loadFragment(lockerFrag)
                }

                R.id.nav_profile -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                }

                R.id.nav_history -> {
                    val historyFrag = HistoryFragment()
                    loadFragment(historyFrag)
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        // Initialize bottom navigation buttons
        profileBtn = findViewById(R.id.account_nav)
        homeBtn = findViewById(R.id.home_nav)
        lockerBtn = findViewById(R.id.lock_nav)
        historyBtn = findViewById(R.id.history_nav)
        scannerBtn = findViewById(R.id.scanner_nav)

        profileBtn.setOnClickListener {
            startActivity(Intent(applicationContext, ProfileActivity::class.java))
        }

        homeBtn.setOnClickListener {
            updateBottomNavigationState(R.id.home_view, R.id.home_tv)
            loadFragment(HomeFragment())
        }

        lockerBtn.setOnClickListener {
            updateBottomNavigationState(R.id.lock_view, R.id.lock_tv)
            loadFragment(LockFragment())
        }

        historyBtn.setOnClickListener {
            updateBottomNavigationState(R.id.history_view, R.id.history_tv)
            loadFragment(HistoryFragment())
        }

        scannerBtn.setOnClickListener {
            //
            checkPermissionCamera(this)
        }

    }

    private fun checkPermissionCamera(context: Context) {
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showCamera()
        } else {
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                Toast.makeText(context, "Camera Permission required", Toast.LENGTH_SHORT).show()
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }
    }

    private fun setResult(string: String) {
        sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        //fetchingData(string)
    }

    private fun showCamera() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("SCAN QR CODE")
        options.setCameraId(0)
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        options.setOrientationLocked(false)
        scanLauncher.launch(options)
    }



    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.home_frame, fragment)
            .commit()
    }

    private fun updateBottomNavigationState(selectedViewId: Int, selectedTextViewId: Int) {
        findViewById<View>(R.id.home_view).backgroundTintList = ColorStateList.valueOf(Color.parseColor("#008475"))
        findViewById<View>(R.id.lock_view).backgroundTintList = ColorStateList.valueOf(Color.parseColor("#008475"))
        findViewById<View>(R.id.history_view).backgroundTintList = ColorStateList.valueOf(Color.parseColor("#008475"))

        findViewById<View>(selectedViewId).backgroundTintList = ColorStateList.valueOf(Color.parseColor("#03534d"))

        findViewById<TextView>(R.id.home_tv).visibility = if (selectedTextViewId == R.id.home_tv) View.VISIBLE else View.GONE
        findViewById<TextView>(R.id.lock_tv).visibility = if (selectedTextViewId == R.id.lock_tv) View.VISIBLE else View.GONE
        findViewById<TextView>(R.id.history_tv).visibility = if (selectedTextViewId == R.id.history_tv) View.VISIBLE else View.GONE
    }

    private fun getAccessToken(): String? {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("access_token", null)
    }
}
package com.ivar7284.rbi_pay

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.ivar7284.rbi_pay.fragments.HistoryFragment
import com.ivar7284.rbi_pay.fragments.HomeFragment
import com.ivar7284.rbi_pay.fragments.LockFragment
import com.ivar7284.rbi_pay.fragments.LoginFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var drawerButton: ImageButton

    private lateinit var homeBtn: LinearLayout
    private lateinit var profileBtn: LinearLayout
    private lateinit var lockerBtn: LinearLayout
    private lateinit var historyBtn: LinearLayout
    private lateinit var scannerBtn: ImageView

    private val URL = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        overridePendingTransition(0, 0)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadFragment(HomeFragment())
        findViewById<View>(R.id.home_view).backgroundTintList = ColorStateList.valueOf(Color.parseColor("#03534d"))
        findViewById<TextView>(R.id.home_tv).visibility = View.VISIBLE

        //nav drawer
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
                R.id.nav_locker ->{
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

        //nav bar bottom
        profileBtn = findViewById(R.id.account_nav)
        homeBtn = findViewById(R.id.home_nav)
        lockerBtn = findViewById(R.id.lock_nav)
        historyBtn = findViewById(R.id.history_nav)
        scannerBtn = findViewById(R.id.scanner_nav)

        profileBtn.setOnClickListener {
            startActivity(Intent(applicationContext, ProfileActivity::class.java))
        }

        homeBtn.setOnClickListener {
            findViewById<View>(R.id.home_view).backgroundTintList = ColorStateList.valueOf(Color.parseColor("#03534d"))
            findViewById<View>(R.id.lock_view).backgroundTintList = ColorStateList.valueOf(Color.parseColor("#008475"))
            findViewById<View>(R.id.history_view).backgroundTintList = ColorStateList.valueOf(Color.parseColor("#008475"))
            findViewById<TextView>(R.id.home_tv).visibility = View.VISIBLE
            findViewById<TextView>(R.id.history_tv).visibility = View.GONE
            findViewById<TextView>(R.id.lock_tv).visibility = View.GONE
            val homeFrag = HomeFragment()
            loadFragment(homeFrag)
        }

        lockerBtn.setOnClickListener {
            findViewById<View>(R.id.lock_view).backgroundTintList = ColorStateList.valueOf(Color.parseColor("#03534d"))
            findViewById<View>(R.id.home_view).backgroundTintList = ColorStateList.valueOf(Color.parseColor("#008475"))
            findViewById<View>(R.id.history_view).backgroundTintList = ColorStateList.valueOf(Color.parseColor("#008475"))
            findViewById<TextView>(R.id.lock_tv).visibility = View.VISIBLE
            findViewById<TextView>(R.id.home_tv).visibility = View.GONE
            findViewById<TextView>(R.id.history_tv).visibility = View.GONE
            val lockerFrag = LockFragment()
            loadFragment(lockerFrag)
        }

        historyBtn.setOnClickListener {
            findViewById<View>(R.id.history_view).backgroundTintList = ColorStateList.valueOf(Color.parseColor("#03534d"))
            findViewById<View>(R.id.home_view).backgroundTintList = ColorStateList.valueOf(Color.parseColor("#008475"))
            findViewById<View>(R.id.lock_view).backgroundTintList = ColorStateList.valueOf(Color.parseColor("#008475"))
            findViewById<TextView>(R.id.history_tv).visibility = View.VISIBLE
            findViewById<TextView>(R.id.home_tv).visibility = View.GONE
            findViewById<TextView>(R.id.lock_tv).visibility = View.GONE
            val historyFrag = HistoryFragment()
            loadFragment(historyFrag)
        }

        scannerBtn.setOnClickListener {
            Toast.makeText(this, "Not yet Implemented", Toast.LENGTH_LONG).show()
        }

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

    private fun getAccessToken(): String? {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("access_token", null)
    }

}
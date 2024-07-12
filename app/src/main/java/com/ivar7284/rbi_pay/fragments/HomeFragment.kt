package com.ivar7284.rbi_pay.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ivar7284.rbi_pay.BankTransferActivity
import com.ivar7284.rbi_pay.MobileTransferActivity
import com.ivar7284.rbi_pay.PaymentActivity
import com.ivar7284.rbi_pay.R
import com.ivar7284.rbi_pay.ReportActivity
import com.ivar7284.rbi_pay.VirtualCreditActivity
import com.ivar7284.rbi_pay.VirtualDebitActivity
import com.ivar7284.rbi_pay.adapters.VideoAdapter
import com.ivar7284.rbi_pay.dataclasses.VideoItem
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

class HomeFragment : Fragment() {

    private lateinit var reportBtn: AppCompatButton
    private lateinit var vCreditCard: Button
    private lateinit var vDebitCard: Button
    private lateinit var scannerBtn: LinearLayout
    private lateinit var mobileTransferBtn: LinearLayout
    private lateinit var bankTransferBtn: LinearLayout

    private lateinit var videoRecyclerView: RecyclerView
    private lateinit var videoAdapter: VideoAdapter
    private val videoList = listOf(
        VideoItem("eyV020bXW4s?si=RwL7r3qqYalxhtyI", "Video 1"),
        VideoItem("AABDnX2xhs8?si=kQdHOJE-jHylIttU", "Video 2"),
        VideoItem("9A6AGfSkjl8?si=LkFeUiy_v3xVqmvK", "Video 3"),
        VideoItem("3RMWM4oNQ8A?si=d7K661i6t5VfZ8bt", "Video 4"),
    )
    private val handler = Handler(Looper.getMainLooper())
    private var currentIndex = 0

    private val runnable = object : Runnable {
        override fun run() {
            if (currentIndex == videoAdapter.itemCount) currentIndex = 0
            videoRecyclerView.smoothScrollToPosition(currentIndex++)
            handler.postDelayed(this, 5000) // 5 seconds delay
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                showCamera()
            } else {
                Toast.makeText(requireContext(), "Camera Permission required", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    private val scanLauncher =
        registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
            run {
                if (result.contents == null) {
                    Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show()
                } else {
                    setResult(result.contents)
                }
            }
        }

    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val views = inflater.inflate(R.layout.fragment_home, container, false)

        //video playback
        videoRecyclerView = views.findViewById(R.id.video_playback_rv)
        videoRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        videoAdapter = VideoAdapter(requireContext(), videoList)
        videoRecyclerView.adapter = videoAdapter

        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        reportBtn = views.findViewById(R.id.report_spam_btn)
        vCreditCard = views.findViewById(R.id.virtual_credit_btn)
        vDebitCard = views.findViewById(R.id.virtual_debit_btn)
        scannerBtn = views.findViewById(R.id.scan_pay_ll)
        mobileTransferBtn = views.findViewById(R.id.mobile_no_ll)
        bankTransferBtn = views.findViewById(R.id.bank_account_ll)

        scannerBtn.setOnClickListener {
            checkPermissionCamera(requireContext())
        }

        reportBtn.setOnClickListener {
            startActivity(Intent(requireContext(), ReportActivity::class.java))
        }

        mobileTransferBtn.setOnClickListener {
            startActivity(Intent(requireContext(), MobileTransferActivity::class.java))
        }

        bankTransferBtn.setOnClickListener {
            startActivity(Intent(requireContext(), BankTransferActivity::class.java))
        }

        vCreditCard.setOnClickListener {
            startActivity(Intent(requireContext(), VirtualCreditActivity::class.java))
        }

        vDebitCard.setOnClickListener {
            startActivity(Intent(requireContext(), VirtualDebitActivity::class.java))
        }


        handler.post(runnable)
        return views
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(runnable)
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
        val intent = Intent(requireContext(), PaymentActivity::class.java)
        intent.putExtra("QR_RESULT", string)
        startActivity(intent)
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

    private fun getAccessToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

}
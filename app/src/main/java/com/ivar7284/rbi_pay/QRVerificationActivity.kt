package com.ivar7284.rbi_pay

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.schedule


class QRVerificationActivity : AppCompatActivity() {

    private lateinit var backBtn: ImageView
    private lateinit var uploadImage: AppCompatButton
    private lateinit var uploadUsingCamera: AppCompatButton
    private lateinit var preview: ImageView
    private lateinit var checkBtn: CircularProgressButton

    private var selectedImageUri: Uri? = null

    private lateinit var cameraImageUri: Uri

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_qrverification)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        uploadImage = findViewById(R.id.upload_photo_btn)
        uploadUsingCamera = findViewById(R.id.upload_camera_btn)
        preview = findViewById(R.id.preview_iv)
        checkBtn = findViewById(R.id.check_button)
        backBtn = findViewById(R.id.back_btn)

        uploadImage.setOnClickListener { openGallery() }
        uploadUsingCamera.setOnClickListener { openCamera() }
        checkBtn.setOnClickListener {
            checkBtn.startAnimation()
            forNow(this)
            //sendImageToServer()
        }
        backBtn.setOnClickListener { finish() }
    }

    private fun forNow(context: Context) {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    checkBtn.revertAnimation()
                    AlertDialog.Builder(context)
                        .setTitle("QR Verification")
                        .setMessage("Your QR code is verified, you can make a payment to this QR code.")
                        .setPositiveButton("ok") { dialogInterface: DialogInterface, _: Int ->
                            dialogInterface.dismiss()
                            startActivity(Intent(applicationContext, HomeActivity::class.java))
                            finish()
                        }
                        .show()
                }
            }
        }, 1000)
    }


    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(galleryIntent)
    }

    private fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val photoFile = createImageFile()
            cameraImageUri = FileProvider.getUriForFile(this, "com.ivar7284.rbi_pay.provider", photoFile)

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
            }
            cameraLauncher.launch(cameraIntent)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            selectedImageUri = result.data!!.data
            preview.visibility = View.VISIBLE
            preview.setImageURI(selectedImageUri)
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, cameraImageUri)
            val rotatedBitmap = rotateBitmap(bitmap, 90f)
            preview.visibility = View.VISIBLE
            preview.setImageBitmap(rotatedBitmap)
            selectedImageUri = cameraImageUri
        }
    }


    private val cameraPermissionLauncher = registerForActivityResult(RequestPermission()) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            showAlertBox()
        }
    }

    private fun showAlertBox() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("Camera permission is required!!")
            .setPositiveButton("ok") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                startActivity(Intent(applicationContext, HomeActivity::class.java))
                finish()
            }
            .show()
    }

    private fun getImageUri(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }

    private fun sendImageToServer() {
        checkBtn.startAnimation()
        selectedImageUri?.let { uri ->
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            val base64Image = encodeImage(bitmap)
            val jsonObject = JSONObject()
            jsonObject.put("image", base64Image)

            val request = JsonObjectRequest(
                Request.Method.POST,
                "",
                jsonObject,
                { response ->
                    Log.d("Volley Response", response.toString())
                    checkBtn.revertAnimation()
                },
                { error ->
                    Log.e("Volley Error", error.toString())
                    checkBtn.revertAnimation()
                }
            )
            Volley.newRequestQueue(this).add(request)
        }
    }

    private fun encodeImage(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    private fun rotateBitmap(bitmap: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}

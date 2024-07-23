package com.ivar7284.rbi_pay

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ivar7284.rbi_pay.utils.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream

class ReportActivity : AppCompatActivity() {

    private lateinit var backBtn: ImageView
    private lateinit var transactionId: EditText
    private lateinit var description: EditText
    private lateinit var image1: ImageView
    private lateinit var image2: ImageView
    private lateinit var reportBtn: CircularProgressButton

    private lateinit var sharedPreferences: SharedPreferences

    private val PICK_IMAGES_REQUEST = 123
    private val selectedImages = mutableListOf<Uri>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_report)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        initializeViews()
        setupImagePickers()

        transactionId.setText(idFromIntent() ?: "")

        reportBtn.setOnClickListener {
            if (isFormValid()) {
                reportBtn.startAnimation()
                sendReport()
            } else {
                showAlertDialog()
            }
        }

        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun initializeViews() {
        backBtn = findViewById(R.id.back_btn)
        transactionId = findViewById(R.id.transaction_id_et)
        description = findViewById(R.id.description_et)
        image1 = findViewById(R.id.image1)
        image2 = findViewById(R.id.image2)
        reportBtn = findViewById(R.id.report_button)
    }

    private fun setupImagePickers() {
        image1.setOnClickListener { pickImages() }
        image2.setOnClickListener { pickImages() }
    }

    private fun idFromIntent(): String? {
        return intent.getStringExtra("transaction_id")
    }

    private fun showCustomAlertDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Reporting")
            .setMessage(message)
            .setPositiveButton("ok") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                startActivity(Intent(applicationContext, HomeActivity::class.java))
                finish()
            }
            .show()
    }

    private fun pickImages() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(intent, PICK_IMAGES_REQUEST)
    }

    private fun imageToRequestBody(partName: String, imageView: ImageView, imageName: String): MultipartBody.Part {
        val drawable = (imageView.drawable as? BitmapDrawable)?.bitmap
        val byteArrayOutputStream = ByteArrayOutputStream()
        drawable?.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), imageBytes)
        Log.d("ImageRequestBody", "Converted image $imageName to RequestBody")
        return MultipartBody.Part.createFormData(partName, imageName, requestBody)
    }

    private fun createPartFromString(value: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), value)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK) {
            data?.let {
                selectedImages.clear()
                if (it.clipData != null) {
                    val count = it.clipData!!.itemCount
                    for (i in 0 until count) {
                        val imageUri = it.clipData!!.getItemAt(i).uri
                        selectedImages.add(imageUri)
                        when (i) {
                            0 -> loadImageIntoView(imageUri, image1)
                            1 -> loadImageIntoView(imageUri, image2)
                        }
                    }
                } else if (it.data != null) {
                    val imageUri = it.data!!
                    selectedImages.add(imageUri)
                    loadImageIntoView(imageUri, image1)
                }
            }
        }
    }

    private fun loadImageIntoView(imageUri: Uri, imageView: ImageView) {
        Glide.with(this)
            .load(imageUri)
            .apply(RequestOptions().override(imageView.width, imageView.height))
            .into(imageView)
    }

    private fun showAlertDialog() {
        AlertDialog.Builder(this)
            .setTitle("All fields are required")
            .setMessage("Please fill all the required fields.")
            .setPositiveButton("OK") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun isFormValid(): Boolean {
        return transactionId.text.toString().isNotEmpty() && description.text.toString().isNotEmpty()
    }

    private fun sendReport() {
        val accessToken = sharedPreferences.getString("access_token", null)
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer $accessToken")
                    .method(original.method, original.body)
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://rbihackathon2024-production.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        Log.i("requestData", selectedImages.toString())

        val transactionIdRequestBody = createPartFromString(transactionId.text.toString())
        val descriptionRequestBody = createPartFromString(description.text.toString())

        val image1Part = imageToRequestBody("product_image_1", image1, "image1.jpg")
        val image2Part = imageToRequestBody("product_image_2", image2, "image2.jpg")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.uploadData(
                    transactionIdRequestBody,
                    descriptionRequestBody,
                    image1Part,
                    image2Part,
                )

                withContext(Dispatchers.Main) {
                    if (response != null) {
                        when (response.code()) {
                            200, 201 -> {
                                reportBtn.revertAnimation()
                                Toast.makeText(applicationContext, "Reported successfully", Toast.LENGTH_SHORT).show()
                                showCustomAlertDialog("Report successful")
                            }
                            else -> {
                                val errorBody = response.errorBody()?.string()
                                reportBtn.revertAnimation()
                                Log.e("uploading error", "Error Code: ${response.code()}, Message: ${response.message()}, Error Body: $errorBody")
                                Toast.makeText(applicationContext, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        reportBtn.revertAnimation()
                        Toast.makeText(applicationContext, "Error: Response is null", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("uploading error", e.message.toString())
                reportBtn.revertAnimation()
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Exception: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

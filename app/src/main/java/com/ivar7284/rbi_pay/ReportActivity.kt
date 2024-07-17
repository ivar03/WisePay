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
import androidx.appcompat.widget.AppCompatButton
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

    //TODO: can add mic for taking the payment amount through speech to text or for search function

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
        //initializing shared prefs
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        //pick images
        image1 = findViewById(R.id.image1)
        image2 = findViewById(R.id.image2)
        image1.setOnClickListener {
            pickImages()
        }
        image2.setOnClickListener {
            pickImages()
        }

        //report
        transactionId = findViewById(R.id.transaction_id_et)
        description = findViewById(R.id.description_et)
        reportBtn = findViewById(R.id.report_button)
        reportBtn.setOnClickListener {
            if(transactionId.text.toString().isEmpty() || description.text.toString().isEmpty()){
                showAlertDialog()
            }else{
                reportBtn.startAnimation()
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

                //setting up retrofit
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://rbihackathon2024-production.up.railway.app/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build()

                val apiService = retrofit.create(ApiService::class.java)
                Log.i("requestData", selectedImages.toString())

                val transactionIdRequestBody = createPartFromString(transactionId.text.toString())
                val descriptionRequestBody = createPartFromString(description.text.toString())

                val image1Part = imageToRequestBody("product_image_1",image1, "image1.jpg")
                val image2Part = imageToRequestBody("product_image_2",image2, "image2.jpg")

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
                                    201 -> {
                                        Log.e("uploading success", "Data uploaded successfully")
                                        reportBtn.revertAnimation()
                                        Toast.makeText(applicationContext, "Data uploaded successfully", Toast.LENGTH_SHORT).show()
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
                        withContext(Dispatchers.Main) {
                            reportBtn.revertAnimation()
                            Log.e("uploading error", "Error: ${e.message.toString()}")
                            Toast.makeText(applicationContext, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        //back button
        backBtn = findViewById(R.id.back_btn)
        backBtn.setOnClickListener {
            finish()
        }

    }

    private fun showCustomAlertDialog(s: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Reporting")
            .setMessage(s)
            .setPositiveButton("ok") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun pickImages(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent,PICK_IMAGES_REQUEST)
    }

    private fun imageToRequestBody(jangoKey: String, imageView: ImageView, imageName: String): MultipartBody.Part {
        val drawable = (imageView.drawable as? BitmapDrawable)?.bitmap
        val byteArrayOutputStream = ByteArrayOutputStream()
        drawable?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        // Use the imageBytes directly to create RequestBody
        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), imageBytes)
        Log.d("ImageRequestBody", "Converted image $imageName to RequestBody")
        //Pass the RequestBody to create MultipartBody.Part
        return MultipartBody.Part.createFormData(jangoKey, imageName, requestBody)
    }

    fun createPartFromString(value: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), value)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == AppCompatActivity.RESULT_OK) {
            data?.let {
                if (it.clipData != null) {
                    val count = it.clipData!!.itemCount
                    for (i in 0 until count) {
                        val imageUri = it.clipData!!.getItemAt(i).uri
                        selectedImages.add(imageUri)
                        when (i) {
                            0 -> Glide.with(this)
                                .load(imageUri)
                                .apply(RequestOptions().override(image1.width, image1.height))
                                .into(image1)
                            1 -> Glide.with(this)
                                .load(imageUri)
                                .apply(RequestOptions().override(image2.width, image2.height))
                                .into(image2)
                        }
                    }
                } else if (it.data != null) {
                    val imageUri = it.data!!
                    selectedImages.add(imageUri)
                    Glide.with(this).load(imageUri).into(image1)
                }
            }
        }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("all_field_are_required")
            .setMessage("you_have_missed_one_or_more_field_please_fill_all_the_fields_as_they_are_required")
            .setPositiveButton("ok") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
            .show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
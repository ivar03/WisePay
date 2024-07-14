package com.ivar7284.rbi_pay

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream

class ReportActivity : AppCompatActivity() {

    //TODO: can add mic for taking the payment amount through speech to text or for search function

    private lateinit var backBtn: ImageView
    private lateinit var transactionId: EditText
    private lateinit var description: EditText
    private lateinit var image1: ImageView
    private lateinit var image2: ImageView
    private lateinit var reportBtn: CircularProgressButton

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
                //
            }
        }

        //back button
        backBtn = findViewById(R.id.back_btn)
        backBtn.setOnClickListener {
            finish()
        }

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
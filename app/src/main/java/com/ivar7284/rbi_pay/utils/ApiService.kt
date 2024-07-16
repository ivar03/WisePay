package com.ivar7284.rbi_pay.utils

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("report/")
    suspend fun uploadData(
        @Part("transaction_id") sellingOffer: RequestBody?,
        @Part("description") additionalOffer: RequestBody?,
        @Part product_image_1: MultipartBody.Part?,
        @Part product_image_2: MultipartBody.Part?,
    ): Response<ResponseBody>
}
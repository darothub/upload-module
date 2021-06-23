package com.darothub.uploadimagemodule.service

import com.darothub.uploadimagemodule.model.UploadImageResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiCall {
    @POST("upload")
    suspend fun uploadImage(@Body file: RequestBody):UploadImageResponse
}


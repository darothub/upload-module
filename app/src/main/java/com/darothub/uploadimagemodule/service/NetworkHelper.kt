package com.darothub.uploadimagemodule.service

import com.google.gson.GsonBuilder
import okhttp3.CacheControl
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkHelper {
    private const val BASE_URL = "https://darot-image-upload-service.herokuapp.com/api/v1/"
    private val networkLogger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val client = okhttp3.OkHttpClient.Builder()
        .addInterceptor(networkLogger)
        .connectTimeout(5, TimeUnit.MINUTES)
        .readTimeout(5, TimeUnit.MINUTES)
        .retryOnConnectionFailure(false)
        .build()

    private val gson: GsonConverterFactory = GsonConverterFactory.create(GsonBuilder().setLenient().create())

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(gson)
        .client(client)
        .build()

    val remoteService = retrofit.create(ApiCall::class.java)
}

package com.bromancelabs.photogallery.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitSingletonKt  {

    private val retrofit: Retrofit? = null

    fun getInstance(url: String) = retrofit ?:
        Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
}
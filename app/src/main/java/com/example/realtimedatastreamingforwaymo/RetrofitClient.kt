package com.example.realtimedatastreamingforwaymo

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val apiService: WaymoApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.waymo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WaymoApiService::class.java)
    }
}
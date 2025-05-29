package com.example.artsy_mobile.interfaces

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SimArtistsInstance {
    val api: SimArtistsInterface by lazy{
        Retrofit.Builder()
            .baseUrl("https://android-backend-final.uw.r.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SimArtistsInterface::class.java)
    }
}
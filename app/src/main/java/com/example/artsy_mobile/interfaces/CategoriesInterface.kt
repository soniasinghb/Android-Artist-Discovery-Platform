package com.example.artsy_mobile.interfaces

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CategoriesInterface {
    @GET("genes/{artworkId}")
    suspend fun fetchCategories(@Path("artworkId") artworkId: String): Response<CategoriesResponse>
}
package com.example.artsy_mobile.interfaces

import retrofit2.Response
import retrofit2.http.GET

interface MeInterface {
    @GET("/auth/me")
    suspend fun fetchMe(): Response<userProfileDetails>
}
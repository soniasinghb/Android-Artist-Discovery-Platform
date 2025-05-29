package com.example.artsy_mobile.interfaces
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface searchArtists {
    @GET("/search")
    //suspend means asynchronous call
    suspend fun fetchArtists(@Query("name") name: String): Response<ArtistsResponse>

}
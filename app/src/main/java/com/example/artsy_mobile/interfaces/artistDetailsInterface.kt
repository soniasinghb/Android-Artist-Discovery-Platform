package com.example.artsy_mobile.interfaces

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface artistDetailsInterface {
    @GET("/artists/{artistId}")
    suspend fun fetchArtistDetails(@Path("artistId") artistId: String): Response<ArtistDetailsResponse>
}
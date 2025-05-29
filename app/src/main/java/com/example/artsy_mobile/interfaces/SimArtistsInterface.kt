package com.example.artsy_mobile.interfaces

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface SimArtistsInterface {
    @GET("/artists/artists/{artistId}")
    suspend fun fetchSimilarArtists(@Path("artistId") artistId: String): Response<SimArtistResponse>
}
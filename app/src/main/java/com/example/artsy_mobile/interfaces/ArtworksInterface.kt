package com.example.artsy_mobile.interfaces

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ArtworksInterface {
    @GET("/artworks/{artistId}")
    suspend fun getArtworks(@Path("artistId") artistId: String): Response<ArtworksResponse>
}
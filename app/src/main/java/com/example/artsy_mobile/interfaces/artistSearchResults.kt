package com.example.artsy_mobile.interfaces

data class ArtistsResponse(
    val result: Boolean,
    val message: List<ArtistSearchResults>
)

data class ArtistSearchResults(
    val artist_ID: String,
    val artist_Image: String,
    val artist_Title: String
)
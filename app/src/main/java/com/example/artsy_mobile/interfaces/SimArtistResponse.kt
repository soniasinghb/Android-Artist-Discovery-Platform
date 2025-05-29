package com.example.artsy_mobile.interfaces

data class SimArtistResponse (
    val result: Boolean,
    val message: List<ArtistSearchResults>
)

//data class simArtistDetails(
//    val artist_ID: String,
//    val artist_Image: String,
//    val artist_Title: String,
//)
package com.example.artsy_mobile.interfaces

data class ArtistDetailsResponse(
    val result: Boolean,
    val message: ArtistDetails
)

data class ArtistDetails(
    val artist_id: String,
    val artist_name: String,
    val artist_bday: String,
    val artist_dday: String,
    val artist_nationality: String,
    val artist_biography: String
)

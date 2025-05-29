package com.example.artsy_mobile.interfaces

data class ArtworksResponse(
    val result: Boolean,
    val message: List<ArtworkDetails>
)

data class ArtworkDetails(
    val artwork_id: String,
    val artwork_title: String,
    val artwork_img: String,
    val artwork_date: String,
)

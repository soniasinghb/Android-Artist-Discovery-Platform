package com.example.artsy_mobile.interfaces

import java.util.Date

data class userProfileDetails (
    val result: Boolean,
    val message: userProfile
)

data class userProfile(
    val _id: String,
    val emailid: String,
    val profileImageUrl: String,
    val favArtists: List<favorite>
)

data class favorite(
    val artistId: String,
    val artistName: String,
    val artistBday: String,
    val artistDday: String,
    val artistNationality: String,
    val artistImg: String,
    val additionTime: Date
)
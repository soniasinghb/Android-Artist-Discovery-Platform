package com.example.artsy_mobile

import android.net.Uri

sealed class Screen(val route: String) {
    object HomeScreen : Screen("home")
    object Login: Screen("login")
    object Register: Screen("register")
    object searchArtists: Screen("searchArtists")
    object artistDetails: Screen("artistDetails/{artistId}/{artistName}"){
        fun createRoute(artistId: String, artistName: String): String {
            return "artistDetails/$artistId/${Uri.encode(artistName)}"
        }
    }
}
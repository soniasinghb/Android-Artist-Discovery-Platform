package com.example.artsy_mobile.interfaces

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.HTTP
import retrofit2.http.POST

interface favsInterface {
    @POST("/favArtists/addFav")
    suspend fun addFav(@Body fav: favorite): Response<favsResp>

    @HTTP(method = "DELETE", path = "/favArtists/removeFav", hasBody = true)
    suspend fun removeFav(@Body artistId: RemoveFavReq): Response<favsResp>
}
data class RemoveFavReq(val artistId: String)


data class favsResp(
    val result: Boolean,
    val message: String
)

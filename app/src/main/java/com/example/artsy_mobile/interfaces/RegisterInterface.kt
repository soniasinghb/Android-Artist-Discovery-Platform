package com.example.artsy_mobile.interfaces

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST

interface RegisterInterface {
    @POST("/auth/register")
    suspend fun register(@Body registerRequest: RegisterReq): Response<RegisterResp>
}

data class RegisterReq(val emailid: String, val password: String, val fullname: String)
data class RegisterResp(val result: Boolean, val message: String)

interface DeleteAccInterface{
    @DELETE("/auth/deleteAccount")
    suspend fun deleteAcc(): Response<DeleteAccResp>
}

data class DeleteAccResp(val result: Boolean, val message: String)
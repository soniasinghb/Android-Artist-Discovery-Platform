package com.example.artsy_mobile.interfaces

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginInterface {
    @POST("/auth/login")
    suspend fun login(@Body loginRequest: LoginReq): Response<LoginResp>
}
data class LoginReq(val emailid: String, val password: String)
data class LoginResp(val result: Boolean, val message: String)

interface LogoutInterface {
    @POST("/auth/logout")
    suspend fun logout(): Response<LogoutResp>
}

data class LogoutResp(val result: Boolean, val message: String)
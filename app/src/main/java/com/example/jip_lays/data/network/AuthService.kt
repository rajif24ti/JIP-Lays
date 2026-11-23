package com.example.jip_lays.data.network

import com.example.jip_lays.data.model.SignUpRequest
import com.example.jip_lays.data.model.SignUpResponse
import com.example.jip_lays.data.model.RefreshTokenRequest
import com.example.jip_lays.data.model.TokenRequest
import com.example.jip_lays.data.model.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {

    @POST("auth/v1/signup")
    suspend fun signUp(
        @Header("apikey") apiKey: String = SupabaseConfig.ANON_KEY,
        @Body body: SignUpRequest
    ): Response<SignUpResponse>

    @POST("auth/v1/token?grant_type=password")
    suspend fun login(
        @Header("apikey") apiKey: String = SupabaseConfig.ANON_KEY,
        @Body body: TokenRequest
    ): Response<TokenResponse>

    @POST("auth/v1/token?grant_type=refresh_token")
    suspend fun refresh(
        @Header("apikey") apiKey: String = SupabaseConfig.ANON_KEY,
        @Body body: RefreshTokenRequest
    ): Response<TokenResponse>
}

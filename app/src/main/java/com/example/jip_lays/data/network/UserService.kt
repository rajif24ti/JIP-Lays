package com.example.jip_lays.data.network

import com.example.jip_lays.data.model.UserModel
import retrofit2.Response
import retrofit2.http.*

interface UserService {

    @GET("rest/v1/users")
    suspend fun getUsers(
        @Header("apikey") apiKey: String = SupabaseConfig.ANON_KEY,
        @Header("Authorization") authHeader: String,
        @Header("Accept-Profile") schema: String = SupabaseConfig.SCHEMA,
        @Query("id") filterId: String? = null
    ): Response<List<UserModel>>

    @POST("rest/v1/users")
    suspend fun insertUser(
        @Header("apikey") apiKey: String = SupabaseConfig.ANON_KEY,
        @Header("Authorization") authHeader: String,
        @Header("Content-Profile") schema: String = SupabaseConfig.SCHEMA,
        @Header("Prefer") prefer: String = "return=representation",
        @Body user: UserModel
    ): Response<List<UserModel>>
}

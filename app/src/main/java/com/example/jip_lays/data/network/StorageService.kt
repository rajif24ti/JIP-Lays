package com.example.jip_lays.data.network

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface StorageService {

    @POST("storage/v1/object/menu-produksi/{path}")
    suspend fun uploadImage(
        @Header("apikey") apiKey: String = SupabaseConfig.ANON_KEY,
        @Header("Authorization") authHeader: String,
        @Header("Content-Type") contentType: String,
        @Path("path") path: String,
        @Body fileBytes: RequestBody
    ): Response<ResponseBody>

    @DELETE("storage/v1/object/menu-produksi/{path}")
    suspend fun deleteImage(
        @Header("apikey") apiKey: String = SupabaseConfig.ANON_KEY,
        @Header("Authorization") authHeader: String,
        @Path("path") path: String
    ): Response<ResponseBody>
}

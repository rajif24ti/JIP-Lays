package com.example.jip_lays.data.network

import com.example.jip_lays.data.model.KaryawanModel
import retrofit2.Response
import retrofit2.http.*

interface KaryawanService {

    @GET("rest/v1/karyawan")
    suspend fun getKaryawan(
        @Header("apikey") apiKey: String = SupabaseConfig.ANON_KEY,
        @Header("Authorization") authHeader: String,
        @Header("Accept-Profile") schema: String = SupabaseConfig.SCHEMA,
        @Query("order") order: String = "id.desc"
    ): Response<List<KaryawanModel>>

    @POST("rest/v1/karyawan")
    suspend fun insertKaryawan(
        @Header("apikey") apiKey: String = SupabaseConfig.ANON_KEY,
        @Header("Authorization") authHeader: String,
        @Header("Content-Profile") schema: String = SupabaseConfig.SCHEMA,
        @Header("Prefer") prefer: String = "return=representation",
        @Body karyawan: KaryawanModel
    ): Response<List<KaryawanModel>>

    @PATCH("rest/v1/karyawan")
    suspend fun updateKaryawan(
        @Header("apikey") apiKey: String = SupabaseConfig.ANON_KEY,
        @Header("Authorization") authHeader: String,
        @Header("Content-Profile") schema: String = SupabaseConfig.SCHEMA,
        @Header("Prefer") prefer: String = "return=representation",
        @Query("id") idFilter: String,
        @Body karyawan: KaryawanModel
    ): Response<List<KaryawanModel>>

    @DELETE("rest/v1/karyawan")
    suspend fun deleteKaryawan(
        @Header("apikey") apiKey: String = SupabaseConfig.ANON_KEY,
        @Header("Authorization") authHeader: String,
        @Header("Content-Profile") schema: String = SupabaseConfig.SCHEMA,
        @Query("id") idFilter: String
    ): Response<Void>
}

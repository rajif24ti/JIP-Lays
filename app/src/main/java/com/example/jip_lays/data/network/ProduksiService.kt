package com.example.jip_lays.data.network

import com.example.jip_lays.data.model.ProduksiModel
import retrofit2.Response
import retrofit2.http.*

interface ProduksiService {

    @GET("rest/v1/produksi")
    suspend fun getProduksi(
        @Header("apikey") apiKey: String = SupabaseConfig.ANON_KEY,
        @Header("Authorization") authHeader: String,
        @Header("Accept-Profile") schema: String = SupabaseConfig.SCHEMA,
        @Query("order") order: String = "id.desc"
    ): Response<List<ProduksiModel>>

    @POST("rest/v1/produksi")
    suspend fun insertProduksi(
        @Header("apikey") apiKey: String = SupabaseConfig.ANON_KEY,
        @Header("Authorization") authHeader: String,
        @Header("Content-Profile") schema: String = SupabaseConfig.SCHEMA,
        @Header("Prefer") prefer: String = "return=representation",
        @Body produksi: ProduksiModel
    ): Response<List<ProduksiModel>>

    @PATCH("rest/v1/produksi")
    suspend fun updateProduksi(
        @Header("apikey") apiKey: String = SupabaseConfig.ANON_KEY,
        @Header("Authorization") authHeader: String,
        @Header("Content-Profile") schema: String = SupabaseConfig.SCHEMA,
        @Header("Prefer") prefer: String = "return=representation",
        @Query("id") idFilter: String,
        @Body produksi: ProduksiModel
    ): Response<List<ProduksiModel>>

    @DELETE("rest/v1/produksi")
    suspend fun deleteProduksi(
        @Header("apikey") apiKey: String = SupabaseConfig.ANON_KEY,
        @Header("Authorization") authHeader: String,
        @Header("Content-Profile") schema: String = SupabaseConfig.SCHEMA,
        @Query("id") idFilter: String
    ): Response<Void>
}

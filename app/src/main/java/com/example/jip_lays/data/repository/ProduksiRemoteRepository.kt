package com.example.jip_lays.data.repository

import com.example.jip_lays.data.model.ProduksiModel
import com.example.jip_lays.data.network.SupabaseClient
import com.example.jip_lays.data.network.SupabaseConfig

class ProduksiRemoteRepository {

    suspend fun getProduksi(token: String?): Result<List<ProduksiModel>> {
        return try {
            val authHeader = if (!token.isNullOrEmpty()) "Bearer $token" else "Bearer ${SupabaseConfig.ANON_KEY}"
            val response = SupabaseClient.produksiService.getProduksi(authHeader = authHeader)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Gagal mengambil data produksi"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun insertProduksi(token: String?, produksi: ProduksiModel): Result<ProduksiModel> {
        return try {
            val authHeader = if (!token.isNullOrEmpty()) "Bearer $token" else "Bearer ${SupabaseConfig.ANON_KEY}"
            val response = SupabaseClient.produksiService.insertProduksi(
                authHeader = authHeader,
                produksi = produksi
            )

            if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                Result.success(response.body()!!.first())
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Gagal menambah produksi"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduksi(token: String?, id: Long, produksi: ProduksiModel): Result<ProduksiModel> {
        return try {
            val authHeader = if (!token.isNullOrEmpty()) "Bearer $token" else "Bearer ${SupabaseConfig.ANON_KEY}"
            val response = SupabaseClient.produksiService.updateProduksi(
                authHeader = authHeader,
                idFilter = "eq.$id",
                produksi = produksi
            )

            if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                Result.success(response.body()!!.first())
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Gagal memperbarui produksi"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduksi(token: String?, id: Long): Result<Boolean> {
        return try {
            val authHeader = if (!token.isNullOrEmpty()) "Bearer $token" else "Bearer ${SupabaseConfig.ANON_KEY}"
            val response = SupabaseClient.produksiService.deleteProduksi(
                authHeader = authHeader,
                idFilter = "eq.$id"
            )

            if (response.isSuccessful) {
                Result.success(true)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Gagal menghapus produksi"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

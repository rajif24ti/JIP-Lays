package com.example.jip_lays.data.repository

import com.example.jip_lays.data.model.KaryawanModel
import com.example.jip_lays.data.network.SupabaseClient
import com.example.jip_lays.data.network.SupabaseConfig

class KaryawanRemoteRepository {

    suspend fun getKaryawan(token: String?): Result<List<KaryawanModel>> {
        return try {
            val authHeader = if (!token.isNullOrEmpty()) "Bearer $token" else "Bearer ${SupabaseConfig.ANON_KEY}"
            val response = SupabaseClient.karyawanService.getKaryawan(authHeader = authHeader)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Gagal mengambil data karyawan"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun insertKaryawan(token: String?, karyawan: KaryawanModel): Result<KaryawanModel> {
        return try {
            val authHeader = if (!token.isNullOrEmpty()) "Bearer $token" else "Bearer ${SupabaseConfig.ANON_KEY}"
            val response = SupabaseClient.karyawanService.insertKaryawan(
                authHeader = authHeader,
                karyawan = karyawan
            )

            if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                Result.success(response.body()!!.first())
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Gagal menambah karyawan"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateKaryawan(token: String?, id: Long, karyawan: KaryawanModel): Result<KaryawanModel> {
        return try {
            val authHeader = if (!token.isNullOrEmpty()) "Bearer $token" else "Bearer ${SupabaseConfig.ANON_KEY}"
            val response = SupabaseClient.karyawanService.updateKaryawan(
                authHeader = authHeader,
                idFilter = "eq.$id",
                karyawan = karyawan
            )

            if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                Result.success(response.body()!!.first())
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Gagal memperbarui karyawan"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteKaryawan(token: String?, id: Long): Result<Boolean> {
        return try {
            val authHeader = if (!token.isNullOrEmpty()) "Bearer $token" else "Bearer ${SupabaseConfig.ANON_KEY}"
            val response = SupabaseClient.karyawanService.deleteKaryawan(
                authHeader = authHeader,
                idFilter = "eq.$id"
            )

            if (response.isSuccessful) {
                Result.success(true)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Gagal menghapus karyawan"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

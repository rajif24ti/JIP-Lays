package com.example.jip_lays.data.repository

import com.example.jip_lays.data.model.KaryawanModel
import com.example.jip_lays.data.network.SupabaseClient
import com.example.jip_lays.data.network.SupabaseConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class KaryawanRemoteRepository {

    suspend fun getKaryawan(token: String?): Result<List<KaryawanModel>> = request {
        val response = SupabaseClient.karyawanService.getKaryawan(authHeader = authHeader(token))
        if (!response.isSuccessful) error(response.errorBody()?.string() ?: "Gagal mengambil data karyawan")
        response.body() ?: error("Respons data karyawan kosong")
    }

    suspend fun insertKaryawan(token: String?, karyawan: KaryawanModel): Result<KaryawanModel> = request {
        val response = SupabaseClient.karyawanService.insertKaryawan(
            authHeader = authHeader(token),
            karyawan = karyawan
        )
        if (!response.isSuccessful) error(response.errorBody()?.string() ?: "Gagal menambah karyawan")
        response.body()?.firstOrNull() ?: error("Supabase tidak mengembalikan data karyawan yang dibuat")
    }

    suspend fun updateKaryawan(token: String?, id: Long, karyawan: KaryawanModel): Result<KaryawanModel> = request {
        val response = SupabaseClient.karyawanService.updateKaryawan(
            authHeader = authHeader(token),
            idFilter = "eq.$id",
            karyawan = karyawan
        )
        if (!response.isSuccessful) error(response.errorBody()?.string() ?: "Gagal memperbarui karyawan")
        response.body()?.firstOrNull() ?: error("Karyawan $id tidak ditemukan saat diperbarui")
    }

    suspend fun deleteKaryawan(token: String?, id: Long): Result<Boolean> = request {
        val response = SupabaseClient.karyawanService.deleteKaryawan(
            authHeader = authHeader(token),
            idFilter = "eq.$id"
        )
        if (!response.isSuccessful) error(response.errorBody()?.string() ?: "Gagal menghapus karyawan")
        true
    }

    private fun authHeader(token: String?): String =
        "Bearer " + (token?.takeIf { it.isNotBlank() } ?: SupabaseConfig.ANON_KEY)

    private suspend fun <T> request(block: suspend () -> T): Result<T> =
        withContext(Dispatchers.IO) {
            try {
                Result.success(block())
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                if (exception is java.io.InterruptedIOException ||
                    (exception is java.io.IOException && exception.message?.contains("Canceled", ignoreCase = true) == true)
                ) {
                    throw CancellationException("Job was cancelled", exception)
                }
                Result.failure(exception)
            }
        }
}

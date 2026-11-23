package com.example.jip_lays.data.repository

import com.example.jip_lays.data.model.KaryawanModel
import com.example.jip_lays.data.network.SupabaseClient
import com.example.jip_lays.data.network.SupabaseConfig
import com.example.jip_lays.data.network.SupabaseErrorHandler
import com.example.jip_lays.data.network.SupabaseSession
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class KaryawanRemoteRepository {

    suspend fun getKaryawan(token: String?): Result<List<KaryawanModel>> = request {
        val response = SupabaseClient.karyawanService.getKaryawan(authHeader = authHeader(token))
        if (!response.isSuccessful) {
            SupabaseErrorHandler.handleUnsuccessfulResponse("GET /rest/v1/karyawan", response)
        }
        response.body() ?: error("Respons data karyawan kosong")
    }

    suspend fun insertKaryawan(token: String?, karyawan: KaryawanModel): Result<KaryawanModel> = request {
        val response = SupabaseClient.karyawanService.insertKaryawan(
            authHeader = authHeader(token),
            karyawan = karyawan
        )
        if (!response.isSuccessful) {
            SupabaseErrorHandler.handleUnsuccessfulResponse("POST /rest/v1/karyawan", response)
        }
        response.body()?.firstOrNull() ?: error("Supabase tidak mengembalikan data karyawan yang dibuat")
    }

    suspend fun updateKaryawan(token: String?, id: Long, karyawan: KaryawanModel): Result<KaryawanModel> = request {
        val response = SupabaseClient.karyawanService.updateKaryawan(
            authHeader = authHeader(token),
            idFilter = "eq.$id",
            karyawan = karyawan
        )
        if (!response.isSuccessful) {
            SupabaseErrorHandler.handleUnsuccessfulResponse("PATCH /rest/v1/karyawan", response)
        }
        response.body()?.firstOrNull() ?: error("Karyawan $id tidak ditemukan saat diperbarui")
    }

    suspend fun deleteKaryawan(token: String?, id: Long): Result<Boolean> = request {
        val response = SupabaseClient.karyawanService.deleteKaryawan(
            authHeader = authHeader(token),
            idFilter = "eq.$id"
        )
        if (!response.isSuccessful) {
            SupabaseErrorHandler.handleUnsuccessfulResponse("DELETE /rest/v1/karyawan", response)
        }
        true
    }

    private suspend fun authHeader(token: String?): String {
        val validToken = SupabaseSession.validAccessToken(token)
        return "Bearer " + (validToken?.takeIf { it.isNotBlank() } ?: SupabaseConfig.ANON_KEY)
    }

    private suspend fun <T> request(block: suspend () -> T): Result<T> =
        withContext(Dispatchers.IO) {
            try {
                Result.success(block())
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Result.failure(SupabaseErrorHandler.formatThrowable(exception))
            }
        }
}

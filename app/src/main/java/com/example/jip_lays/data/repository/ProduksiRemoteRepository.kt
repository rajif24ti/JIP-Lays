package com.example.jip_lays.data.repository

import com.example.jip_lays.data.model.ProduksiModel
import com.example.jip_lays.data.network.SupabaseClient
import com.example.jip_lays.data.network.SupabaseConfig
import com.example.jip_lays.data.network.SupabaseErrorHandler
import com.example.jip_lays.data.network.SupabaseSession
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProduksiRemoteRepository {

    suspend fun getProduksi(token: String?): Result<List<ProduksiModel>> = request {
        val response = SupabaseClient.produksiService.getProduksi(authHeader = authHeader(token))
        if (!response.isSuccessful) {
            SupabaseErrorHandler.handleUnsuccessfulResponse("GET /rest/v1/produksi", response)
        }
        response.body() ?: error("Respons data produksi kosong")
    }

    suspend fun insertProduksi(token: String?, produksi: ProduksiModel): Result<ProduksiModel> = request {
        val response = SupabaseClient.produksiService.insertProduksi(
            authHeader = authHeader(token),
            produksi = produksi
        )
        if (!response.isSuccessful) {
            SupabaseErrorHandler.handleUnsuccessfulResponse("POST /rest/v1/produksi", response)
        }
        response.body()?.firstOrNull() ?: error("Supabase tidak mengembalikan data produksi yang dibuat")
    }

    suspend fun updateProduksi(token: String?, id: Long, produksi: ProduksiModel): Result<ProduksiModel> = request {
        val response = SupabaseClient.produksiService.updateProduksi(
            authHeader = authHeader(token),
            idFilter = "eq.$id",
            produksi = produksi
        )
        if (!response.isSuccessful) {
            SupabaseErrorHandler.handleUnsuccessfulResponse("PATCH /rest/v1/produksi", response)
        }
        response.body()?.firstOrNull() ?: error("Produksi $id tidak ditemukan saat diperbarui")
    }

    suspend fun deleteProduksi(token: String?, id: Long): Result<Boolean> = request {
        val response = SupabaseClient.produksiService.deleteProduksi(
            authHeader = authHeader(token),
            idFilter = "eq.$id"
        )
        if (!response.isSuccessful) {
            SupabaseErrorHandler.handleUnsuccessfulResponse("DELETE /rest/v1/produksi", response)
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

package com.example.jip_lays.data.repository

import android.content.Context
import android.net.Uri
import com.example.jip_lays.data.network.SupabaseClient
import com.example.jip_lays.data.network.SupabaseConfig
import com.example.jip_lays.data.network.SupabaseErrorHandler
import com.example.jip_lays.data.network.SupabaseSession
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID

class StorageRepository {

    suspend fun uploadMenuImage(context: Context, token: String?, imageUri: Uri): Result<String> = request {
        val mimeType = context.contentResolver.getType(imageUri) ?: "image/jpeg"
        require(mimeType.startsWith("image/")) { "Berkas yang dipilih bukan gambar" }
        val bytes = context.contentResolver.openInputStream(imageUri)?.use { it.readBytes() }
            ?: error("Gagal membaca berkas gambar")
        val extension = when (mimeType.substringAfter('/').lowercase()) {
            "jpeg" -> "jpg"
            "png", "webp", "heic", "heif" -> mimeType.substringAfter('/').lowercase()
            else -> "jpg"
        }
        val fileName = "menu_${UUID.randomUUID()}.$extension"
        val response = SupabaseClient.storageService.uploadImage(
            authHeader = authHeader(token),
            contentType = mimeType,
            path = fileName,
            fileBytes = bytes.toRequestBody(mimeType.toMediaType())
        )
        if (!response.isSuccessful) {
            SupabaseErrorHandler.handleUnsuccessfulResponse("POST /storage/v1/object/menu-produksi", response)
        }
        "${SupabaseConfig.BASE_URL}storage/v1/object/public/menu-produksi/$fileName"
    }

    suspend fun deleteMenuImage(token: String?, imageUrl: String): Result<Unit> = request {
        val fileName = imageUrl.substringBefore('?').substringAfterLast("/menu-produksi/")
        require(fileName.isNotBlank() && fileName != imageUrl) { "Path gambar Storage tidak valid" }
        val response = SupabaseClient.storageService.deleteImage(
            authHeader = authHeader(token),
            path = fileName
        )
        if (!response.isSuccessful) {
            SupabaseErrorHandler.handleUnsuccessfulResponse("DELETE /storage/v1/object/menu-produksi", response)
        }
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

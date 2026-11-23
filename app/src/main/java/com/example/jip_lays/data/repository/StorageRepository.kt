package com.example.jip_lays.data.repository

import android.content.Context
import android.net.Uri
import com.example.jip_lays.data.network.SupabaseClient
import com.example.jip_lays.data.network.SupabaseConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID

class StorageRepository {

    suspend fun uploadMenuImage(context: Context, token: String?, imageUri: Uri): Result<String> = runCatching {
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
            error(response.errorBody()?.string() ?: "Gagal mengunggah gambar ke Storage")
        }
        "${SupabaseConfig.BASE_URL}storage/v1/object/public/menu-produksi/$fileName"
    }

    suspend fun deleteMenuImage(token: String?, imageUrl: String): Result<Unit> = runCatching {
        val fileName = imageUrl.substringBefore('?').substringAfterLast("/menu-produksi/")
        require(fileName.isNotBlank() && fileName != imageUrl) { "Path gambar Storage tidak valid" }
        val response = SupabaseClient.storageService.deleteImage(
            authHeader = authHeader(token),
            path = fileName
        )
        if (!response.isSuccessful) {
            error(response.errorBody()?.string() ?: "Gagal menghapus gambar dari Storage")
        }
    }

    private fun authHeader(token: String?): String =
        "Bearer ${token?.takeIf { it.isNotBlank() } ?: SupabaseConfig.ANON_KEY}"
}

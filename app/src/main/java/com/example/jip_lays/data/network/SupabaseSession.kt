package com.example.jip_lays.data.network

import android.content.Context
import com.example.jip_lays.data.model.RefreshTokenRequest

object SupabaseSession {

    suspend fun validAccessToken(fallbackToken: String?): String? {
        val context = SupabaseConfig.applicationContext() ?: return fallbackToken
        val preferences = context.getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val accessToken = preferences.getString("access_token", null) ?: fallbackToken ?: return null
        val expiresAt = preferences.getLong("access_token_expires_at", 0L)
        if (expiresAt == 0L || expiresAt > System.currentTimeMillis() + 60_000L) return accessToken

        val refreshToken = preferences.getString("refresh_token", null)
            ?: return accessToken
        val response = SupabaseClient.authService.refresh(
            body = RefreshTokenRequest(refreshToken)
        )
        if (!response.isSuccessful) {
            error(response.errorBody()?.string() ?: "Sesi Supabase kedaluwarsa dan gagal diperbarui")
        }
        val refreshed = response.body() ?: error("Respons refresh sesi Supabase kosong")
        preferences.edit()
            .putString("access_token", refreshed.accessToken)
            .putString("refresh_token", refreshed.refreshToken ?: refreshToken)
            .putLong(
                "access_token_expires_at",
                System.currentTimeMillis() + refreshed.expiresIn * 1_000L
            )
            .apply()
        return refreshed.accessToken
    }
}

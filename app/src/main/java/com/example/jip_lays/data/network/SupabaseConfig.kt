package com.example.jip_lays.data.network

import android.content.Context
import com.example.jip_lays.BuildConfig

object SupabaseConfig {

    // Konfigurasi URL dan Anon Key Supabase
    var BASE_URL: String = normalizeUrl(BuildConfig.SUPABASE_URL)
    var ANON_KEY: String = BuildConfig.SUPABASE_ANON_KEY
    const val SCHEMA = "mobile"

    fun init(context: Context) {
        val prefs = context.getSharedPreferences("supabase_config", Context.MODE_PRIVATE)
        val customUrl = prefs.getString("supabase_url", null)
        val customKey = prefs.getString("supabase_key", null)

        if (!customUrl.isNullOrEmpty()) {
            BASE_URL = normalizeUrl(customUrl)
        }
        if (!customKey.isNullOrEmpty()) {
            ANON_KEY = customKey
        }
    }

    fun saveConfig(context: Context, url: String, key: String) {
        val cleanUrl = normalizeUrl(url)
        BASE_URL = cleanUrl
        ANON_KEY = key
        context.getSharedPreferences("supabase_config", Context.MODE_PRIVATE)
            .edit()
            .putString("supabase_url", cleanUrl)
            .putString("supabase_key", key)
            .apply()
    }

    private fun normalizeUrl(url: String): String {
        var cleanUrl = url.trim()
            .removeSuffix("/rest/v1")
            .removeSuffix("/rest/v1/")
            .removeSuffix("/")
        if (cleanUrl.isNotBlank() && !cleanUrl.contains("://")) {
            cleanUrl = "https://$cleanUrl"
        }
        return if (cleanUrl.endsWith("/")) cleanUrl else "$cleanUrl/"
    }
}

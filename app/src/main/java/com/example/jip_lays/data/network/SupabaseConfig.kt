package com.example.jip_lays.data.network

import android.content.Context

object SupabaseConfig {

    // Konfigurasi URL dan Anon Key Supabase
    var BASE_URL: String = "https://your-supabase-project.supabase.co/"
    var ANON_KEY: String = "your-supabase-anon-key"
    const val SCHEMA = "mobile"

    fun init(context: Context) {
        val prefs = context.getSharedPreferences("supabase_config", Context.MODE_PRIVATE)
        val customUrl = prefs.getString("supabase_url", null)
        val customKey = prefs.getString("supabase_key", null)

        if (!customUrl.isNullOrEmpty()) {
            BASE_URL = if (customUrl.endsWith("/")) customUrl else "$customUrl/"
        }
        if (!customKey.isNullOrEmpty()) {
            ANON_KEY = customKey
        }
    }

    fun saveConfig(context: Context, url: String, key: String) {
        val cleanUrl = if (url.endsWith("/")) url else "$url/"
        BASE_URL = cleanUrl
        ANON_KEY = key
        context.getSharedPreferences("supabase_config", Context.MODE_PRIVATE)
            .edit()
            .putString("supabase_url", cleanUrl)
            .putString("supabase_key", key)
            .apply()
    }
}

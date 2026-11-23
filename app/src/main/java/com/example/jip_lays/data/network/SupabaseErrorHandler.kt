package com.example.jip_lays.data.network

import android.util.Log
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

object SupabaseErrorHandler {

    fun formatThrowable(throwable: Throwable): Throwable {
        return when (throwable) {
            is UnknownHostException -> {
                Exception("Tidak ada koneksi internet atau DNS gagal terhubung ke Supabase. Pastikan internet emulator/HP aktif.")
            }
            is ConnectException -> {
                Exception("Koneksi jaringan ke server Supabase ditolak atau terputus.")
            }
            is SocketTimeoutException -> {
                Exception("Koneksi ke Supabase Waktu Habis (Timeout). Periksa kecepatan internet Anda.")
            }
            is SSLHandshakeException -> {
                Exception("Koneksi aman SSL ke Supabase gagal. Periksa tanggal/waktu di perangkat Anda.")
            }
            else -> throwable
        }
    }

    fun <T> handleUnsuccessfulResponse(endpointName: String, response: Response<T>): Nothing {
        val statusCode = response.code()
        val rawErrorBody = response.errorBody()?.string()?.take(500) ?: ""

        Log.e("SupabaseAPI", "Error HTTP $statusCode pada endpoint $endpointName: $rawErrorBody")

        val explanation = when (statusCode) {
            400 -> "Format data request tidak valid (HTTP 400 Bad Request)."
            401 -> "Masalah autentikasi atau API key tidak valid (HTTP 401 Unauthorized)."
            403 -> "Akses ditolak oleh RLS (Row Level Security) atau izin tidak mencukupi (HTTP 403 Forbidden)."
            404 -> "Endpoint, schema 'mobile', atau tabel tidak ditemukan di Supabase (HTTP 404 Not Found)."
            406 -> "Header Accept-Profile/Content-Profile 'mobile' tidak didukung atau disetujui (HTTP 406 Not Acceptable)."
            500, 502, 503 -> "Terjadi kendala pada server database Supabase (HTTP $statusCode Internal Error)."
            else -> "Gagal terhubung ke Supabase (HTTP status code $statusCode)."
        }

        val detailMessage = if (rawErrorBody.isNotBlank()) "\nDetail: $rawErrorBody" else ""
        error("[$endpointName] $explanation$detailMessage")
    }
}

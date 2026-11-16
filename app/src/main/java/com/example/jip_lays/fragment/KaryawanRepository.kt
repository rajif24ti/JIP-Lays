package com.example.jip_lays.fragment

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Karyawan(
    val nama: String,
    val divisi: String
)

data class AbsensiHariIni(
    val tanggal: String,
    val jamMasuk: String?,
    val jamPulang: String?
)

object KaryawanRepository {

    val daftarKaryawan = listOf(
        Karyawan("Budi Santoso", "Divisi Produksi"),
        Karyawan("Siti Rahayu", "Divisi Produksi"),
        Karyawan("Andi Saputra", "Divisi Logistik/Persiapan"),
        Karyawan("Dewi Lestari", "Divisi Pengemasan & Pemorsian"),
        Karyawan("Ahmad Hidayat", "Divisi Distribusi"),
        Karyawan("Rina Wijaya", "Divisi Kebersihan"),
        Karyawan("Bambang Pamungkas", "Divisi Logistik/Persiapan"),
        Karyawan("Eko Prasetyo", "Divisi Produksi"),
        Karyawan("Fitriani Utami", "Divisi Pengemasan & Pemorsian"),
        Karyawan("Hendra Gunawan", "Divisi Distribusi"),
        Karyawan("Indah Permata", "Divisi Kebersihan"),
        Karyawan("Joko Susilo", "Divisi Logistik/Persiapan"),
        Karyawan("Kartika Sari", "Divisi Produksi"),
        Karyawan("Lukman Hakim", "Divisi Pengemasan & Pemorsian"),
        Karyawan("Maya Anggraini", "Divisi Distribusi"),
        Karyawan("Nurhadi", "Divisi Kebersihan"),
        Karyawan("Oscar Pratama", "Divisi Logistik/Persiapan"),
        Karyawan("Putri Maharani", "Divisi Produksi"),
        Karyawan("Rizky Ramadhan", "Divisi Pengemasan & Pemorsian"),
        Karyawan("Tri Wahyuni", "Divisi Distribusi")
    )

    val daftarDivisi = listOf(
        "Divisi Produksi",
        "Divisi Pengemasan & Pemorsian",
        "Divisi Distribusi",
        "Divisi Kebersihan",
        "Divisi Logistik/Persiapan"
    )

    private val dateKey: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    fun tanggalHariIni(): String =
        SimpleDateFormat("dd MMMM yyyy", Locale.forLanguageTag("id-ID")).format(Date())

    fun waktuSekarang(): String =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

    fun absensiHariIni(context: Context, nama: String): AbsensiHariIni {
        val preferences = context.getSharedPreferences("karyawan_absensi", Context.MODE_PRIVATE)
        val key = "${dateKey}_$nama"
        return AbsensiHariIni(
            tanggal = tanggalHariIni(),
            jamMasuk = preferences.getString("${key}_masuk", null),
            jamPulang = preferences.getString("${key}_pulang", null)
        )
    }

    fun catatJamMasuk(context: Context, nama: String): Boolean =
        catatWaktu(context, nama, "masuk")

    fun catatJamPulang(context: Context, nama: String): Boolean =
        catatWaktu(context, nama, "pulang")

    private fun catatWaktu(context: Context, nama: String, jenis: String): Boolean {
        val preferences = context.getSharedPreferences("karyawan_absensi", Context.MODE_PRIVATE)
        val key = "${dateKey}_$nama"
        val waktuKey = "${key}_$jenis"
        if (preferences.contains(waktuKey)) return false
        return preferences.edit().putString(waktuKey, waktuSekarang()).commit()
    }
}

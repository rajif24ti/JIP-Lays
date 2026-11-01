package com.example.jip_lays

data class KerusakanModel(
    val id: String = System.currentTimeMillis().toString(),
    val namaPeralatan: String,
    val lokasiDivisi: String,
    val deskripsi: String,
    val tingkatKepentingan: String, // Rendah, Sedang, Tinggi, Darurat
    val photoPath: String?,
    val tanggal: String
)

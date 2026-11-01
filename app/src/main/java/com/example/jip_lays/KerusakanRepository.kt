package com.example.jip_lays

object KerusakanRepository {
    val daftarKerusakan = mutableListOf<KerusakanModel>(
        KerusakanModel(
            namaPeralatan = "Mesin Penanak Nasi (Rice Cooker Besar)",
            lokasiDivisi = "Divisi Memasak",
            deskripsi = "Pemanas bawah tidak berfungsi optimal, nasi bagian bawah mudah gosong.",
            tingkatKepentingan = "Tinggi",
            photoPath = null,
            tanggal = "01 November 2026"
        ),
        KerusakanModel(
            namaPeralatan = "Mixer Adonan Kapasitas 20L",
            lokasiDivisi = "Divisi Persiapan",
            deskripsi = "Kecamatan gigi pengaduk mengeluarkan bunyi berderit saat kecepatan tinggi.",
            tingkatKepentingan = "Sedang",
            photoPath = null,
            tanggal = "31 Oktober 2026"
        )
    )
}

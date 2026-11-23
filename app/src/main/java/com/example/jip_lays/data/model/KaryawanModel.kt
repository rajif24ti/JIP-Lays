package com.example.jip_lays.data.model

import com.google.gson.annotations.SerializedName

data class KaryawanModel(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("nama") val nama: String,
    @SerializedName("divisi") val divisi: String,
    @SerializedName("jabatan") val jabatan: String? = null,
    @SerializedName("telepon") val telepon: String? = null,
    @SerializedName("alamat") val alamat: String? = null,
    @SerializedName("status_keaktifan") val statusKeaktifan: String? = "Aktif",
    @SerializedName("created_at") val createdAt: String? = null
)

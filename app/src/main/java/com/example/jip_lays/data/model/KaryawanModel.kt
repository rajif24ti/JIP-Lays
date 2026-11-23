package com.example.jip_lays.data.model

import com.google.gson.annotations.SerializedName

data class KaryawanModel(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("nama") val nama: String,
    @SerializedName("divisi") val divisi: String,
    @SerializedName("created_at") val createdAt: String? = null
)

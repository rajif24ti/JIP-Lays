package com.example.jip_lays.data.model

import com.google.gson.annotations.SerializedName

data class ProduksiModel(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("sekolah") val sekolah: String,
    @SerializedName("menu") val menu: String,
    @SerializedName("porsi") val porsi: String,
    @SerializedName("status") val status: String,
    @SerializedName("created_at") val createdAt: String? = null
)

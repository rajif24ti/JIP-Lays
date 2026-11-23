package com.example.jip_lays.data.model

import com.google.gson.annotations.SerializedName

data class NutrisiModel(
    @SerializedName("energi") val energi: Double? = null,
    @SerializedName("protein") val protein: Double? = null,
    @SerializedName("karbohidrat") val karbohidrat: Double? = null,
    @SerializedName("lemak") val lemak: Double? = null,
    @SerializedName("serat") val serat: Double? = null,
    @SerializedName("kalsium") val kalsium: Double? = null,
    @SerializedName("zat_besi") val zatBesi: Double? = null
)

data class SekolahPorsiModel(
    @SerializedName("sekolah") val sekolah: String,
    @SerializedName("porsi") val porsi: Int
)

data class ProduksiModel(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("menu") val menu: String,
    @SerializedName("gambar_url") val gambarUrl: String? = null,
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("porsi") val porsi: Int,
    @SerializedName("status") val status: String,
    @SerializedName("gizi") val gizi: NutrisiModel? = null,
    @SerializedName("sekolah_tujuan") val sekolahTujuan: List<SekolahPorsiModel>? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

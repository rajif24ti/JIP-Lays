package com.example.jip_lays.data.model

import com.google.gson.annotations.SerializedName

data class UserModel(
    @SerializedName("id") val id: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("nama") val nama: String? = null,
    @SerializedName("handphone") val handphone: String? = null,
    @SerializedName("role") val role: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

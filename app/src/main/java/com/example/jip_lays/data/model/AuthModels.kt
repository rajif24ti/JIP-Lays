package com.example.jip_lays.data.model

import com.google.gson.annotations.SerializedName

data class UserMetadata(
    @SerializedName("nama") val nama: String,
    @SerializedName("handphone") val handphone: String,
    @SerializedName("role") val role: String
)

data class SignUpRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("data") val data: UserMetadata
)

data class UserAuthInfo(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String?,
    @SerializedName("user_metadata") val userMetadata: UserMetadata?
)

data class SignUpResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("user") val user: UserAuthInfo?
)

data class TokenRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Long,
    @SerializedName("refresh_token") val refreshToken: String?,
    @SerializedName("user") val user: UserAuthInfo?
)

package com.example.jip_lays.data.repository

import com.example.jip_lays.data.model.*
import com.example.jip_lays.data.network.SupabaseClient
import com.example.jip_lays.data.network.SupabaseConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository {

    suspend fun signUp(
        email: String,
        password: String,
        nama: String,
        handphone: String,
        role: String
    ): Result<SignUpResponse> = withContext(Dispatchers.IO) {
        try {
            val metadata = UserMetadata(nama = nama, handphone = handphone, role = role)
            val request = SignUpRequest(email = email, password = password, data = metadata)
            val response = SupabaseClient.authService.signUp(body = request)

            if (response.isSuccessful && response.body() != null) {
                val signUpData = response.body()!!
                val userId = signUpData.id ?: signUpData.user?.id

                // Direct insert into mobile.users as fallback in case DB trigger is not active
                if (!userId.isNullOrEmpty()) {
                    try {
                        val authHeader = if (!signUpData.accessToken.isNullOrEmpty()) {
                            "Bearer ${signUpData.accessToken}"
                        } else {
                            "Bearer ${SupabaseConfig.ANON_KEY}"
                        }

                        val userModel = UserModel(
                            id = userId,
                            email = email,
                            nama = nama,
                            handphone = handphone,
                            role = role
                        )
                        SupabaseClient.userService.insertUser(
                            authHeader = authHeader,
                            user = userModel
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                Result.success(signUpData)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Registrasi gagal"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (e is java.io.InterruptedIOException || (e is java.io.IOException && e.message?.contains("Canceled", ignoreCase = true) == true)) {
                throw CancellationException("Job was cancelled", e)
            }
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<TokenResponse> = withContext(Dispatchers.IO) {
        try {
            val request = TokenRequest(email = email, password = password)
            val response = SupabaseClient.authService.login(body = request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Login gagal"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (e is java.io.InterruptedIOException || (e is java.io.IOException && e.message?.contains("Canceled", ignoreCase = true) == true)) {
                throw CancellationException("Job was cancelled", e)
            }
            Result.failure(e)
        }
    }
}

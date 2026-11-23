package com.example.jip_lays.data.repository

import com.example.jip_lays.data.model.SignUpRequest
import com.example.jip_lays.data.model.SignUpResponse
import com.example.jip_lays.data.model.TokenRequest
import com.example.jip_lays.data.model.TokenResponse
import com.example.jip_lays.data.model.UserMetadata
import com.example.jip_lays.data.network.SupabaseClient
import com.example.jip_lays.data.network.SupabaseErrorHandler
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
    ): Result<SignUpResponse> = request {
        val metadata = UserMetadata(nama = nama, handphone = handphone, role = role)
        val response = SupabaseClient.authService.signUp(
            body = SignUpRequest(email = email, password = password, data = metadata)
        )
        if (!response.isSuccessful) {
            SupabaseErrorHandler.handleUnsuccessfulResponse("POST /auth/v1/signup", response)
        }
        response.body() ?: error("Respons registrasi kosong")
    }

    suspend fun login(email: String, password: String): Result<TokenResponse> = request {
        val response = SupabaseClient.authService.login(
            body = TokenRequest(email = email, password = password)
        )
        if (!response.isSuccessful) {
            SupabaseErrorHandler.handleUnsuccessfulResponse("POST /auth/v1/token", response)
        }
        response.body() ?: error("Respons login kosong")
    }

    private suspend fun <T> request(block: suspend () -> T): Result<T> =
        withContext(Dispatchers.IO) {
            try {
                Result.success(block())
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Result.failure(SupabaseErrorHandler.formatThrowable(exception))
            }
        }
}

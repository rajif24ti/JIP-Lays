package com.example.jip_lays.data.repository

import com.example.jip_lays.data.model.UserModel
import com.example.jip_lays.data.network.SupabaseClient
import com.example.jip_lays.data.network.SupabaseConfig
import com.example.jip_lays.data.network.SupabaseErrorHandler
import com.example.jip_lays.data.network.SupabaseSession
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRemoteRepository {

    suspend fun getUsers(token: String?, userIdFilter: String? = null): Result<List<UserModel>> = request {
        val filterQuery = userIdFilter?.let { "eq.$it" }
        val response = SupabaseClient.userService.getUsers(
            authHeader = authHeader(token),
            filterId = filterQuery
        )
        if (!response.isSuccessful) {
            SupabaseErrorHandler.handleUnsuccessfulResponse("GET /rest/v1/users", response)
        }
        response.body() ?: error("Respons data pengguna kosong")
    }

    private suspend fun authHeader(token: String?): String {
        val validToken = SupabaseSession.validAccessToken(token)
        return "Bearer " + (validToken?.takeIf { it.isNotBlank() } ?: SupabaseConfig.ANON_KEY)
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

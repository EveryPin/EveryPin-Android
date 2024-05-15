package everypin.app.network.api

import everypin.app.network.dto.auth.TokenResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AuthenticationApi {
    @GET("/api/authentication/login")
    suspend fun login(
        @Query("platformCode") platformCode: String,
        @Query("accessToken") accessToken: String
    ): Response<TokenResponse>
}
package everypin.app.network.api

import everypin.app.data.model.LoginRequest
import everypin.app.network.model.auth.TokenRefreshRequest
import everypin.app.network.model.auth.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/api/auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<TokenResponse>

    @POST("/api/auth/refresh")
    suspend fun refresh(@Body tokenRefreshRequest: TokenRefreshRequest): Response<TokenResponse>
}
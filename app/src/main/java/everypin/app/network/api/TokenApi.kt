package everypin.app.network.api

import everypin.app.network.model.auth.TokenRefreshRequest
import everypin.app.network.model.auth.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenApi {
    @POST("/api/token/refresh")
    suspend fun refresh(@Body tokenRefreshRequest: TokenRefreshRequest): Response<TokenResponse>
}
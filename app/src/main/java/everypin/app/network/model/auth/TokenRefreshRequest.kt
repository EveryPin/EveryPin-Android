package everypin.app.network.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenRefreshRequest(
    @SerialName(value = "accessToken")
    val accessToken: String?,
    @SerialName(value = "refreshToken")
    val refreshToken: String?
)
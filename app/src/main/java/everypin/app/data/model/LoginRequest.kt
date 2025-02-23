package everypin.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "platformCode")
    val platformCode: String,
    @Json(name = "accessToken")
    val accessToken: String,
    @Json(name = "fcmToken")
    val fcmToken: String
)

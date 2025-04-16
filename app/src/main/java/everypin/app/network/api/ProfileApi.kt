package everypin.app.network.api

import everypin.app.network.constant.AUTHORIZATION_ACCESS_TOKEN
import everypin.app.network.model.profile.ProfileDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface ProfileApi {
    @GET("/api/profile/me")
    @Headers(AUTHORIZATION_ACCESS_TOKEN)
    suspend fun getProfileMe(): Response<ProfileDto>
}
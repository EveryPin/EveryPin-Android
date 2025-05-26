package everypin.app.network.api

import everypin.app.network.constant.AUTHORIZATION_ACCESS_TOKEN
import everypin.app.network.model.profile.ProfileDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part
import retrofit2.http.Query

interface ProfileApi {
    @GET("/api/profile/me")
    @Headers(AUTHORIZATION_ACCESS_TOKEN)
    suspend fun getProfileMe(): Response<ProfileDto>

    @PATCH("/api/profile/me")
    @Headers(AUTHORIZATION_ACCESS_TOKEN)
    suspend fun updateProfile(
        @Query("ProfileDisplayId") profileDisplayId: String,
        @Query("Name") name: String,
        @Query("SelfIntroduction") selfIntroduction: String,
        @Query("IsPhotoUpload") isPhotoUpload: Boolean = false
    ): Response<String>

    @Multipart
    @PATCH("/api/profile/me")
    @Headers(AUTHORIZATION_ACCESS_TOKEN)
    suspend fun updateProfile(
        @Query("ProfileDisplayId") profileDisplayId: String,
        @Query("Name") name: String,
        @Query("SelfIntroduction") selfIntroduction: String,
        @Part file: MultipartBody.Part,
        @Query("IsPhotoUpload") isPhotoUpload: Boolean = true
    ): Response<String>
}
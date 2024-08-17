package everypin.app.network.api

import everypin.app.network.constant.AUTHORIZATION_ACCESS_TOKEN
import everypin.app.network.dto.post.PostDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path

interface PostApi {
    @GET("/api/post")
    @Headers(AUTHORIZATION_ACCESS_TOKEN)
    suspend fun getPostList(): Response<List<PostDto>>

    @Multipart
    @POST("/api/post")
    @Headers(AUTHORIZATION_ACCESS_TOKEN)
    suspend fun writePost(
        @Part("PostContent") postContent: RequestBody,
        @Part("X") x: RequestBody,
        @Part("Y") y: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): Response<Unit>

    @GET("api/post/{postId}")
    @Headers(AUTHORIZATION_ACCESS_TOKEN)
    suspend fun getPost(
        @Path("postId") postId: Int
    ): Response<PostDto>
}
package everypin.app.network.api

import everypin.app.network.cache.Cacheable
import everypin.app.network.constant.AUTHORIZATION_ACCESS_TOKEN
import everypin.app.network.model.post.PostDetailDto
import everypin.app.network.model.post.PostWriteResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface PostApi {
    @Multipart
    @POST("/api/post")
    @Headers(AUTHORIZATION_ACCESS_TOKEN)
    suspend fun writePost(
        @Part("postContent") postContent: RequestBody,
        @Part("X") x: RequestBody,
        @Part("Y") y: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): Response<PostWriteResponse>

    @GET("/api/post/{postId}")
    @Headers(AUTHORIZATION_ACCESS_TOKEN)
    @Cacheable(15, TimeUnit.MINUTES)
    suspend fun getPost(
        @Path("postId") postId: Int
    ): Response<PostDetailDto>
}
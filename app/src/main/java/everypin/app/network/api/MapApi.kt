package everypin.app.network.api

import everypin.app.network.cache.Cacheable
import everypin.app.network.constant.AUTHORIZATION_ACCESS_TOKEN
import everypin.app.network.model.post.PostDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface MapApi {
    @GET("/api/map/pin")
    @Headers(AUTHORIZATION_ACCESS_TOKEN)
    @Cacheable(15, TimeUnit.MINUTES)
    suspend fun getPinsByRange(
        @Query("x") x: Double,
        @Query("y") y: Double,
        @Query("range") range: Double
    ): Response<List<PostDto>>
}
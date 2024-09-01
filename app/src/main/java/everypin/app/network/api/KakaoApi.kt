package everypin.app.network.api

import everypin.app.network.model.kakao.KakaoLocalSearchKeywordResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoApi {
    @GET("/v2/local/search/keyword.json")
    suspend fun localSearchKeyword(
        @Query("query") query: String,
        @Query("category_group_code") categoryGroupCode: String? = null,
        @Query("x") x: String? = null,
        @Query("y") y: String? = null,
        @Query("radius") radius: Int? = null,
        @Query("rect") rect: String? = null,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("sort") sort: String? = null
    ): Response<KakaoLocalSearchKeywordResponse>
}
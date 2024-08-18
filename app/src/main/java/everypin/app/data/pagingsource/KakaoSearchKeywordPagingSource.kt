package everypin.app.data.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import everypin.app.core.utils.Logger
import everypin.app.data.model.PlaceInfo
import everypin.app.network.api.KakaoApi
import retrofit2.HttpException

class KakaoSearchKeywordPagingSource(
    private val kakaoApi: KakaoApi,
    private val query: String
) : PagingSource<Int, PlaceInfo>() {
    override fun getRefreshKey(state: PagingState<Int, PlaceInfo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PlaceInfo> {
        try {
            val key = params.key ?: 1
            val response =
                kakaoApi.localSearchKeyword(query = query, page = key, size = params.loadSize)
            val data = response.body()
            if (response.isSuccessful && data != null) {
                val placeInfo = data.documents?.filter {
                    it.placeName != null && it.addressName != null && it.x != null && it.y != null
                }?.map {
                    PlaceInfo(
                        placeName = it.placeName!!,
                        addressName = if (it.roadAddressName.isNullOrEmpty()) {
                            it.addressName!!
                        } else {
                            it.roadAddressName
                        },
                        lat = it.y!!.toDouble(),
                        lng = it.x!!.toDouble()
                    )
                } ?: emptyList()
                return LoadResult.Page(
                    data = placeInfo,
                    prevKey = if (key == 1) null else key - 1,
                    nextKey = if (data.meta?.isEnd == true) null else key + 1
                )
            } else {
                throw HttpException(response)
            }
        } catch (e: Exception) {
            Logger.e("카카오 키워드 검색 에러", e)
            return LoadResult.Error(e)
        }
    }
}
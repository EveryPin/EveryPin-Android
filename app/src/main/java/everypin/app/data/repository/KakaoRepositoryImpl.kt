package everypin.app.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import everypin.app.data.model.PlaceInfo
import everypin.app.data.pagingsource.KakaoSearchKeywordPagingSource
import everypin.app.network.api.KakaoApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class KakaoRepositoryImpl @Inject constructor(
    private val kakaoApi: KakaoApi
) : KakaoRepository {
    override fun searchKeywordResultPagingData(
        address: String
    ): Flow<PagingData<PlaceInfo>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = false,
                initialLoadSize = 15
            ),
            pagingSourceFactory = {
                KakaoSearchKeywordPagingSource(
                    kakaoApi = kakaoApi,
                    query = address
                )
            }
        ).flow
    }
}
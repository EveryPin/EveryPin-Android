package everypin.app.data.repository

import androidx.paging.PagingData
import everypin.app.data.model.KakaoSearchKeywordResult
import kotlinx.coroutines.flow.Flow

interface KakaoRepository {
    fun searchKeywordResultPagingData(
        address: String
    ): Flow<PagingData<KakaoSearchKeywordResult>>
}
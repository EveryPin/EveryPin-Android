package everypin.app.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import everypin.app.core.utils.Logger
import everypin.app.data.model.PlaceInfo
import everypin.app.data.repository.KakaoRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchPlaceViewModel @Inject constructor(
    private val kakaoRepository: KakaoRepository
): ViewModel() {
    private val _placeInfoPagingDataState =
        MutableStateFlow<PagingData<PlaceInfo>>(PagingData.empty())
    val placeInfoPagingDataState get() = _placeInfoPagingDataState.asStateFlow()

    var searchValue = MutableStateFlow("")

    init {
        initAutoSearch()
    }

    @OptIn(FlowPreview::class)
    private fun initAutoSearch() {
        searchValue.debounce(300L).distinctUntilChanged().onEach { fetchAddressSearch(it) }
            .launchIn(viewModelScope)
    }

    private fun fetchAddressSearch(value: String) {
        if (value.isEmpty()) return

        viewModelScope.launch {
            kakaoRepository.searchKeywordResultPagingData(value).cachedIn(viewModelScope).catch {
                Logger.e("주소 검색 실패", it)
            }.collectLatest {
                _placeInfoPagingDataState.emit(it)
            }
        }
    }
}
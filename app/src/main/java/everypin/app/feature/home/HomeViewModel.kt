package everypin.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import everypin.app.core.utils.Logger
import everypin.app.data.model.PostPin
import everypin.app.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ln

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {
    private val _postPinsState = MutableStateFlow<List<PostPin>>(emptyList())
    val postPinsState = _postPinsState.asStateFlow()

    fun fetchRangePostList(x: Double, y: Double, range: Double) {
        viewModelScope.launch {
            postRepository.getRangePostPins(lng = x, lat = y, range = range).catch {
                Logger.e("게시물 목록을 가져오는데 실패", it)
            }.collectLatest {
                _postPinsState.emit(it)
            }
        }
    }
}
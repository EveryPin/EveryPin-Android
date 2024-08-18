package everypin.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import everypin.app.core.utils.Logger
import everypin.app.data.model.PostModel
import everypin.app.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {
    private val _postListState = MutableStateFlow<List<PostModel>>(emptyList())
    val postListState = _postListState.asStateFlow()

    fun fetchPostList() {
        viewModelScope.launch {
            postRepository.getPostList().catch {
                Logger.e("게시물 목록을 가져오는데 실패", it)
            }.collectLatest {
                _postListState.emit(it)
            }
        }
    }
}
package everypin.app.feature.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import everypin.app.core.ui.state.UIState
import everypin.app.core.utils.Logger
import everypin.app.data.model.PostDetail
import everypin.app.data.repository.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = PostDetailViewModel.Factory::class)
class PostDetailViewModel @AssistedInject constructor(
    @Assisted("id") private val id: Int,
    private val postRepository: PostRepository
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("id") id: Int): PostDetailViewModel
    }

    private val _postDetailState = MutableStateFlow<UIState<PostDetail>>(UIState.Loading)
    val postDetailState = _postDetailState.asStateFlow()

    init {
        fetchPostDetail()
    }

    private fun fetchPostDetail() {
        viewModelScope.launch(Dispatchers.IO) {
            postRepository.getPost(id).catch {
                Logger.e(it.message.toString(), it)
                _postDetailState.value = UIState.Error(it)
            }.collectLatest {
                _postDetailState.value = UIState.Success(it)
            }
        }
    }
}
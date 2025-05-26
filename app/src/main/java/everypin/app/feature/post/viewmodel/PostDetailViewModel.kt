package everypin.app.feature.post.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import everypin.app.core.helper.GeocoderHelper
import everypin.app.data.repository.PostRepository
import everypin.app.feature.post.state.PostDetailState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

@HiltViewModel(assistedFactory = PostDetailViewModel.Factory::class)
class PostDetailViewModel @AssistedInject constructor(
    @Assisted("id") private val id: Int,
    private val postRepository: PostRepository,
    private val geocoderHelper: GeocoderHelper
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("id") id: Int): PostDetailViewModel
    }

    private val _uiState = MutableStateFlow<PostDetailState>(PostDetailState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchPostDetail()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchPostDetail() {
        viewModelScope.launch {
            postRepository.getPost(id).catch {
                logcat(LogPriority.ERROR) { it.asLog() }
                _uiState.value = PostDetailState.Error(it)
            }.map { postDetail ->
                val address =
                    geocoderHelper.getAddressFromLocation(postDetail.latitude, postDetail.longitude)
                        .catch {
                            logcat(LogPriority.ERROR) { it.asLog() }
                            emit("")
                        }.first()
                PostDetailState.Success(postDetail, address)
            }.collectLatest {
                _uiState.value = it
            }
        }
    }
}
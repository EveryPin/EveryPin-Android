package everypin.app.feature.post.state

import everypin.app.data.model.PostDetail

sealed class PostDetailState {
    data object Loading : PostDetailState()
    data class Success(
        val postDetail: PostDetail,
        val address: String
    ) : PostDetailState()

    data class Error(val error: Throwable) : PostDetailState()
}
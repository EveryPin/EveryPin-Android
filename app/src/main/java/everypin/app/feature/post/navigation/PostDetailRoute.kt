package everypin.app.feature.post.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import everypin.app.feature.post.view.PostDetailScreen
import everypin.app.feature.post.viewmodel.PostDetailViewModel
import kotlinx.serialization.Serializable

@Serializable
data class PostDetailRoute(val id: Int)

@Composable
fun PostDetailRoute(
    postDetailViewModel: PostDetailViewModel,
    onBack: () -> Unit
) {
    val uiState by postDetailViewModel.uiState.collectAsStateWithLifecycle()

    PostDetailScreen(
        onBack = onBack,
        uiState = uiState
    )
}
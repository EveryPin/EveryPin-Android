package everypin.app.feature.post

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data class PostDetailRoute(val id: Int)

fun NavController.navigateToPostDetail(id: Int, navOptions: NavOptions? = null) {
    navigate(PostDetailRoute(id = id), navOptions)
}

fun NavGraphBuilder.postGraph(
    navController: NavController
) {
    composable<PostDetailRoute> { backStackEntry ->
        val id = backStackEntry.toRoute<PostDetailRoute>().id

        val postDetailViewModel = hiltViewModel<PostDetailViewModel, PostDetailViewModel.Factory> {
            it.create(id)
        }

        PostDetailScreen(
            postDetailViewModel = postDetailViewModel,
            onBack = {
                navController.popBackStack()
            }
        )
    }
}
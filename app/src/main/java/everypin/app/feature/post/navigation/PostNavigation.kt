package everypin.app.feature.post.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import everypin.app.feature.post.viewmodel.PostDetailViewModel

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

        PostDetailRoute(
            postDetailViewModel = postDetailViewModel,
            onBack = {
                navController.popBackStack()
            }
        )
    }
}
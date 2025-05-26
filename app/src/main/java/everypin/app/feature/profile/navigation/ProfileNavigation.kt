package everypin.app.feature.profile.navigation

import androidx.compose.material3.SnackbarResult
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import everypin.app.feature.profile.view.ProfileEditRoute
import kotlinx.serialization.Serializable

@Serializable
object ProfileEditRoute

fun NavController.navigateToProfileEdit(navOptions: NavOptions? = null) {
    navigate(ProfileEditRoute, navOptions)
}

fun NavGraphBuilder.profileGraph(
    navController: NavController,
    onShowSnackbar: suspend (String, String?) -> SnackbarResult,
) {
    composable<ProfileEditRoute> {
        ProfileEditRoute(
            onShowSnackbar = onShowSnackbar
        )
    }
}
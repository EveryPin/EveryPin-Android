package everypin.app.feature.signin

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

fun NavController.navigateSignIn(navOptions: NavOptions) {
    navigate(SignInRoute.route, navOptions)
}

fun NavGraphBuilder.signInNavGraph(
    onNavigateToHome: () -> Unit
) {
    composable(route = SignInRoute.route) {
        SignInRoute(
            onClickSignIn = {
                onNavigateToHome()
            }
        )
    }
}

object SignInRoute {
    const val route = "sign_in"
}
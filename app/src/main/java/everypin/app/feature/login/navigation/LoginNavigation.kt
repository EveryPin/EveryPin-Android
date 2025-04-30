package everypin.app.feature.login.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import everypin.app.core.ui.navigation.navigateToHome
import everypin.app.feature.login.view.LoginRoute
import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

fun NavController.navigateToLogin() {
    navigate(LoginRoute) {
        popUpTo(0) {
            inclusive = true
        }
        launchSingleTop = true
    }
}

fun NavGraphBuilder.loginGraph(
    navController: NavController
) {
    composable<LoginRoute> {
        LoginRoute(
            onNavigateToHome = {
                navController.navigateToHome(
                    navOptions = navOptions {
                        popUpTo(0) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                )
            }
        )
    }
}
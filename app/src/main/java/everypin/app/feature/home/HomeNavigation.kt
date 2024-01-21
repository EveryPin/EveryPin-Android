package everypin.app.feature.home

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import everypin.app.core.ui.animation.animateComposable

fun NavController.navigateHome(navOptions: NavOptions) {
    navigate(HomeRoute.route, navOptions)
}

fun NavController.navigateNotification() {
    navigate(HomeRoute.notification())
}

fun NavGraphBuilder.homeNavGraph(
    innerPadding: PaddingValues,
    onBack: () -> Unit,
    onNavigateToNotification: () -> Unit,
    onNavigateToChatList: () -> Unit
) {
    navigation(
        startDestination = HomeRoute.map(),
        route = HomeRoute.route
    ) {
        composable(
            route = HomeRoute.map(),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            HomeScreen(
                innerPadding = innerPadding,
                onNavigateToNotification = onNavigateToNotification,
                onNavigateToChatList = onNavigateToChatList
            )
        }
    }
    animateComposable(
        route = HomeRoute.notification()
    ) {
        NotificationScreen(
            innerPadding = innerPadding,
            onBack = onBack
        )
    }
}

object HomeRoute {
    const val route = "home"

    fun map() = "$route/map"
    fun notification() = "$route/notification"
}
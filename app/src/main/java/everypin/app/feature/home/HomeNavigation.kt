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
import everypin.app.feature.chat.navigateChatList
import everypin.app.feature.post.navigateToPostDetail

fun NavController.navigateHome(navOptions: NavOptions) {
    navigate(HomeRoute.ROUTE, navOptions)
}

fun NavController.navigateNotification() {
    navigate(HomeRoute.notification())
}

fun NavGraphBuilder.homeNavGraph(
    innerPadding: PaddingValues,
    navController: NavController
) {
    navigation(
        startDestination = HomeRoute.map(),
        route = HomeRoute.ROUTE
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
                onNavigateToNotification = {
                    navController.navigateNotification()
                },
                onNavigateToChatList = {
                    navController.navigateChatList()
                },
                onNavigateToPostDetail = {
                    navController.navigateToPostDetail(it)
                }
            )
        }
    }
    animateComposable(
        route = HomeRoute.notification()
    ) {
        NotificationScreen(
            innerPadding = innerPadding,
            onBack = {
                navController.popBackStack()
            }
        )
    }
}

object HomeRoute {
    const val ROUTE = "home"

    fun map() = "$ROUTE/map"
    fun notification() = "$ROUTE/notification"
}
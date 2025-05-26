package everypin.app.core.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import everypin.app.EveryPinAppState
import everypin.app.feature.chat.chatGraph
import everypin.app.feature.login.navigation.LoginRoute
import everypin.app.feature.login.navigation.loginGraph
import everypin.app.feature.notification.notificationGraph
import everypin.app.feature.post.navigation.postGraph
import everypin.app.feature.profile.navigation.profileGraph
import everypin.app.feature.setting.settingGraph

private const val PAGE_TURN_DURATION_MS = 450

@Composable
fun EveryPinNavHost(
    appState: EveryPinAppState,
    innerPadding: PaddingValues,
    onShowSnackbar: suspend (String, String?) -> SnackbarResult
) {
    NavHost(
        navController = appState.navController,
        startDestination = LoginRoute,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(PAGE_TURN_DURATION_MS)
            ) + fadeIn(animationSpec = tween(PAGE_TURN_DURATION_MS))
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(durationMillis = PAGE_TURN_DURATION_MS),
                targetOffset = { it / 2 }
            ) + fadeOut(animationSpec = tween(PAGE_TURN_DURATION_MS))
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(durationMillis = PAGE_TURN_DURATION_MS),
                initialOffset = { it / 2 }
            ) + fadeIn(animationSpec = tween(PAGE_TURN_DURATION_MS))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(PAGE_TURN_DURATION_MS)
            ) + fadeOut(animationSpec = tween(PAGE_TURN_DURATION_MS))
        }
    ) {
        loginGraph(navController = appState.navController)
        topLevelGraph(
            navController = appState.navController,
            innerPadding = innerPadding,
            onShowSnackbar = onShowSnackbar
        )
        profileGraph(
            navController = appState.navController,
            onShowSnackbar = onShowSnackbar
        )
        notificationGraph(navController = appState.navController)
        settingGraph(navController = appState.navController)
        chatGraph(navController = appState.navController)
        postGraph(navController = appState.navController)
    }
}
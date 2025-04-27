package everypin.app.core.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import everypin.app.core.ui.EveryPinAppState
import everypin.app.feature.chat.chatGraph
import everypin.app.feature.notification.notificationGraph
import everypin.app.feature.post.navigation.postGraph
import everypin.app.feature.setting.settingGraph

@Composable
fun EveryPinNavHost(
    appState: EveryPinAppState,
    innerPadding: PaddingValues,
    onShowSnackbar: suspend (String, String?) -> Unit
) {
    NavHost(
        navController = appState.navController,
        startDestination = HomeRoute,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { fadeOut() }
    ) {
        topLevelGraph(
            navController = appState.navController,
            innerPadding = innerPadding,
            onShowSnackbar = onShowSnackbar
        )
        notificationGraph(navController = appState.navController)
        settingGraph(navController = appState.navController)
        chatGraph(navController = appState.navController)
        postGraph(navController = appState.navController)
    }
}
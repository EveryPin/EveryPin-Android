package everypin.app.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import everypin.app.core.ui.navigation.AddPinRoute
import everypin.app.core.ui.navigation.topLevelRoutes

@Composable
fun rememberEveryPinAppState(
    navController: NavHostController
): EveryPinAppState {
    return remember(
        navController
    ) {
        EveryPinAppState(
            navController = navController
        )
    }
}

@Stable
class EveryPinAppState(
    internal val navController: NavHostController
) {
    private val previousDestination = mutableStateOf<NavDestination?>(null)

    val currentDestination: NavDestination?
        @Composable get() {
            val currentEntry =
                navController.currentBackStackEntryFlow.collectAsState(initial = null)

            return currentEntry.value?.destination.also { destination ->
                if (destination != null) {
                    previousDestination.value = destination
                }
            } ?: previousDestination.value
        }

    val shouldShowBottomBar: Boolean
        @Composable get() {
            if (currentDestination?.hasRoute(AddPinRoute::class) == true) return false
            return topLevelRoutes.any { currentDestination?.hasRoute(it.route::class) == true }
        }
}
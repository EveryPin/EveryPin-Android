package everypin.app.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import everypin.app.core.event.AuthEvent
import everypin.app.core.event.AuthEventBus
import everypin.app.core.ui.navigation.AddPinRoute
import everypin.app.core.ui.navigation.navigateToHome
import everypin.app.core.ui.navigation.topLevelRoutes
import everypin.app.core.utils.Logger
import everypin.app.datastore.DataStorePreferences
import everypin.app.datastore.PreferencesKey
import everypin.app.feature.login.navigation.navigateToLogin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun rememberEveryPinAppState(
    navController: NavHostController,
    dataStorePreferences: DataStorePreferences,
    authEventBus: AuthEventBus,
    coroutineScope: CoroutineScope
): EveryPinAppState {
    return remember(
        navController
    ) {
        EveryPinAppState(
            navController = navController,
            dataStorePreferences = dataStorePreferences,
            authEventBus = authEventBus,
            coroutineScope = coroutineScope
        )
    }
}

@Stable
class EveryPinAppState(
    internal val navController: NavHostController,
    internal val dataStorePreferences: DataStorePreferences,
    internal val authEventBus: AuthEventBus,
    internal val coroutineScope: CoroutineScope
) {
    private val previousDestination = mutableStateOf<NavDestination?>(null)

    var shouldShowSplash = true
        private set

    init {
        handleAuthEvent()
        checkAuthentication()
    }

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

    private fun handleAuthEvent() {
        coroutineScope.launch {
            authEventBus.event.collect { event ->
                when (event) {
                    AuthEvent.AuthExpired -> {
                        Logger.d("토큰 만료 이벤트 발생")
                        navController.navigateToLogin()
                    }

                    AuthEvent.SignOut -> {
                        Logger.d("로그아웃 이벤트 발생")
                        navController.navigateToLogin()
                    }

                    AuthEvent.Authenticated -> {
                        Logger.d("인증됨")
                        navController.navigateToHome(
                            navOptions = navOptions {
                                popUpTo(0) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        )
                        delay(30)
                        shouldShowSplash = false
                    }

                    AuthEvent.NotAuthenticated -> {
                        Logger.d("인증되지 않음")
                        shouldShowSplash = false
                    }
                }
            }
        }
    }

    private fun checkAuthentication() {
        coroutineScope.launch {
            val token = dataStorePreferences.getString(PreferencesKey.ACCESS_TOKEN).first()
            if (token != null) {
                authEventBus.notifyAuthenticated()
            } else {
                authEventBus.notifyNotAuthenticated()
            }
        }
    }
}
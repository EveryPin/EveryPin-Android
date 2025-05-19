package everypin.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import everypin.app.core.ui.navigation.AddPinRoute
import everypin.app.core.ui.navigation.EveryPinNavHost
import everypin.app.core.ui.navigation.navigateToAddPin
import everypin.app.core.ui.navigation.topLevelRoutes

@Composable
internal fun EveryPinApp(
    appState: EveryPinAppState
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = appState.shouldShowBottomBar,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                MainNavBar(
                    currentDestination = appState.currentDestination,
                    onClick = { route ->
                        if (route is AddPinRoute) {
                            appState.navController.navigateToAddPin()
                        } else {
                            appState.navController.navigate(route) {
                                popUpTo(appState.navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                restoreState = true
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(
                snackbarHostState,
                modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            EveryPinNavHost(
                appState = appState,
                innerPadding = innerPadding,
                onShowSnackbar = { message, actionLabel ->
                    snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = actionLabel,
                        duration = SnackbarDuration.Short,
                    )
                }
            )
        }
    }
}

@Composable
fun MainNavBar(
    modifier: Modifier = Modifier,
    currentDestination: NavDestination?,
    onClick: (Any) -> Unit
) {
    NavigationBar(
        modifier = modifier
    ) {
        topLevelRoutes.forEach { topLevelRoute ->
            val selected = currentDestination?.hierarchy?.any {
                it.hasRoute(topLevelRoute.route::class)
            } == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    onClick(topLevelRoute.route)
                },
                icon = {
                    Icon(
                        painter = painterResource(
                            id = if (selected) {
                                topLevelRoute.selectedIconId
                            } else {
                                topLevelRoute.unselectedIconId
                            }
                        ),
                        contentDescription = stringResource(topLevelRoute.labelId),
                        tint = if (selected) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = topLevelRoute.labelId),
                        color = if (selected) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        fontWeight = if (selected) {
                            FontWeight.SemiBold
                        } else {
                            FontWeight.Medium
                        }
                    )
                }
            )
        }
    }
}
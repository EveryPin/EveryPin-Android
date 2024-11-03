package everypin.app.feature.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import everypin.app.R
import everypin.app.core.ui.navigation.GlobalNavigation
import everypin.app.core.ui.navigation.GlobalNavigationHandler
import everypin.app.feature.chat.chatGraph
import everypin.app.feature.login.LoginActivity
import everypin.app.feature.notification.notificationGraph
import everypin.app.feature.post.postGraph
import everypin.app.feature.setting.settingGraph
import java.util.UUID

@Composable
internal fun MainScreen(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    DisposableEffect(key1 = Unit) {
        val handler = object : GlobalNavigationHandler {
            override fun onNavigateToSignIn() {
                LoginActivity.startActivityWithClearAllTasks(context.applicationContext)
            }
        }
        val key = "MainScreen_${UUID.randomUUID()}"
        GlobalNavigation.addHandler(key, handler)

        onDispose {
            GlobalNavigation.removeHandler(key)
        }
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = currentDestination?.shouldShowBottomBar() == true,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                MainNavBar(
                    currentDestination = currentDestination,
                    onClick = { route ->
                        if (route is AddPinRoute) {
                            navController.navigateToAddPin()
                        } else {
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                restoreState = true
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            NavHost(
                navController = navController,
                startDestination = HomeRoute,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() },
                popEnterTransition = { fadeIn() },
                popExitTransition = { fadeOut() }
            ) {
                topLevelGraph(
                    navController = navController,
                    innerPadding = innerPadding
                )
                notificationGraph(navController = navController)
                settingGraph(navController = navController)
                chatGraph(navController = navController)
                postGraph(navController = navController)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    modifier: Modifier = Modifier,
    onClickNotification: () -> Unit,
    onClickChat: () -> Unit
) {
    TopAppBar(
        title = {
            Text(stringResource(id = R.string.app_name))
        },
        modifier = modifier,
        actions = {
            IconButton(
                onClick = onClickNotification
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_notifications),
                    contentDescription = stringResource(id = R.string.notification),
                )
            }
            IconButton(
                onClick = onClickChat
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chat),
                    contentDescription = stringResource(id = R.string.chat),
                )
            }
        }
    )
}

private fun NavDestination.shouldShowBottomBar(): Boolean {
    if (hasRoute(AddPinRoute::class)) return false

    return topLevelRoutes.any { hasRoute(it.route::class) }
}
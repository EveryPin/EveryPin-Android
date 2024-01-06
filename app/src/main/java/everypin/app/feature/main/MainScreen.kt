package everypin.app.feature.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import everypin.app.feature.addpin.addPinNavGraph
import everypin.app.feature.addpin.navigateAddPin
import everypin.app.feature.home.HomeRoute
import everypin.app.feature.home.homeNavGraph
import everypin.app.feature.home.navigateHome
import everypin.app.feature.home.navigateNotification
import everypin.app.feature.my.myNavGraph
import everypin.app.feature.my.navigateMy
import everypin.app.feature.signin.SignInRoute
import everypin.app.feature.signin.navigateSignIn
import everypin.app.feature.signin.signInNavGraph

@Composable
internal fun MainScreen(
    navController: NavHostController = rememberNavController(),
    mainViewModel: MainViewModel
) {
    val authState by mainViewModel.authState.collectAsStateWithLifecycle()

    DisposableEffect(authState) {
        val listener = NavController.OnDestinationChangedListener { _, _, _ ->
            if (authState != AuthState.LOADING) mainViewModel.hideSplashScreen()
        }
        navController.addOnDestinationChangedListener(listener)

        if (authState == AuthState.NOT_AUTHENTICATED) {
            navController.navigateSignIn(navOptions {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
            })
        }

        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()

            MainNavBar(
                visible = shouldShowBottomNavigationBar(navBackStackEntry?.destination?.route),
                currentDestination = navBackStackEntry?.destination,
                onClickTab = {
                    onClickMainNavTab(navController, it, navBackStackEntry?.destination?.route)
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            NavHost(
                navController = navController,
                startDestination = HomeRoute.route
            ) {
                signInNavGraph(
                    onNavigateToHome = {
                        navController.navigateHome(navOptions {
                            popUpTo(SignInRoute.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        })
                    }
                )
                homeNavGraph(
                    innerPadding = innerPadding,
                    onBack = {
                        navController.popBackStack()
                    },
                    onNavigateToNotification = {
                        navController.navigateNotification()
                    },
                    onNavigateToChat = {

                    }
                )
                addPinNavGraph(
                    onBack = {
                        navController.popBackStack()
                    }
                )
                myNavGraph(
                    innerPadding = innerPadding
                )
            }
        }
    }
}

private fun shouldShowBottomNavigationBar(route: String?): Boolean {
    if (MainTab.ADD_PIN.route == route) return false

    return MainTab.entries.any { route?.startsWith(it.route) == true }
}

private fun onClickMainNavTab(
    navController: NavHostController,
    tab: MainTab,
    currentRoute: String?
) {
    val startDestinationId = navController.graph.findStartDestination().id

    when (tab) {
        MainTab.HOME -> {
            if (currentRoute == HomeRoute.map()) return

            navController.navigateHome(navOptions {
                popUpTo(startDestinationId) {
                    saveState = true
                }
                restoreState = currentRoute?.startsWith(tab.route) != true
                launchSingleTop = true
            })
        }

        MainTab.ADD_PIN -> navController.navigateAddPin()
        MainTab.MY -> navController.navigateMy(navOptions {
            popUpTo(startDestinationId) {
                saveState = true
            }
            restoreState = true
            launchSingleTop = true
        })
    }
}
package everypin.app.core.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import everypin.app.R
import everypin.app.feature.addpin.AddPinScreen
import everypin.app.feature.chat.navigateToChatList
import everypin.app.feature.home.HomeScreen
import everypin.app.feature.my.MyScreen
import everypin.app.feature.notification.navigateToNotification
import everypin.app.feature.post.navigation.navigateToPostDetail
import everypin.app.feature.setting.navigateToSetting
import kotlinx.serialization.Serializable

data class TopLevelRoute<T : Any>(
    @StringRes val labelId: Int,
    @DrawableRes val selectedIconId: Int,
    @DrawableRes val unselectedIconId: Int,
    val route: T
)

@Serializable
object HomeRoute

@Serializable
object AddPinRoute

@Serializable
object MyRoute

val topLevelRoutes = listOf(
    TopLevelRoute(
        labelId = R.string.home,
        selectedIconId = R.drawable.ic_home_selected,
        unselectedIconId = R.drawable.ic_home_unselected,
        route = HomeRoute,
    ),
    TopLevelRoute(
        labelId = R.string.add_pin,
        selectedIconId = R.drawable.ic_add_location_alt_selected,
        unselectedIconId = R.drawable.ic_add_location_alt_unselected,
        route = AddPinRoute
    ),
    TopLevelRoute(
        labelId = R.string.my,
        selectedIconId = R.drawable.ic_account_circle_selected,
        unselectedIconId = R.drawable.ic_account_circle_unselected,
        route = MyRoute
    )
)

fun NavController.navigateToHome(navOptions: NavOptions) {
    navigate(HomeRoute, navOptions)
}

fun NavController.navigateToAddPin() {
    navigate(AddPinRoute)
}

fun NavController.navigateToMy(navOptions: NavOptions) {
    navigate(MyRoute, navOptions)
}

fun NavGraphBuilder.topLevelGraph(
    navController: NavController,
    innerPadding: PaddingValues,
    onShowSnackbar: suspend (String, String?) -> Unit
) {
    composable<HomeRoute>(
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        HomeScreen(
            innerPadding = innerPadding,
            onNavigateToNotification = {
                navController.navigateToNotification()
            },
            onNavigateToChatList = {
                navController.navigateToChatList()
            },
            onNavigateToPostDetail = {
                navController.navigateToPostDetail(it)
            }
        )
    }
    composable<AddPinRoute> {
        AddPinScreen(
            onShowSnackbar = onShowSnackbar
        )
    }
    composable<MyRoute>(
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        MyScreen(
            innerPadding = innerPadding,
            onShowSnackbar = onShowSnackbar,
            onNavigateToSetting = {
                navController.navigateToSetting()
            }
        )
    }
}
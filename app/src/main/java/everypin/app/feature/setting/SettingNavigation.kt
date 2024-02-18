package everypin.app.feature.setting

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import everypin.app.core.ui.animation.animateComposable

fun NavController.navigateSetting() {
    navigate(SettingRoute.menu())
}

fun NavGraphBuilder.settingNavGraph(
    onBack: () -> Unit
) {
    navigation(
        startDestination = SettingRoute.menu(),
        route = SettingRoute.route
    ) {
        animateComposable(
            route = SettingRoute.menu()
        ) {
            SettingScreen(
                onBack = onBack
            )
        }
    }
}

object SettingRoute {
    const val route = "setting"

    fun menu() = "$route/menu"
}
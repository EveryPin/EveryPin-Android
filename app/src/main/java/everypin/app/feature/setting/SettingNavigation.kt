package everypin.app.feature.setting

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import everypin.app.core.ui.animation.animateComposable
import kotlinx.serialization.Serializable

@Serializable
object SettingNavigation

@Serializable
object SettingRoute

fun NavController.navigateToSetting() {
    navigate(SettingRoute)
}

fun NavGraphBuilder.settingGraph(navController: NavController) {
    navigation<SettingNavigation>(
        startDestination = SettingRoute
    ) {
        animateComposable<SettingRoute> {
            SettingScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
package everypin.app.feature.setting

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
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
        composable<SettingRoute> {
            SettingScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
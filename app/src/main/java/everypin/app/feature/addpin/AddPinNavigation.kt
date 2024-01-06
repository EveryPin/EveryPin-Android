package everypin.app.feature.addpin

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavController.navigateAddPin() {
    navigate(AddPinRoute.route)
}

fun NavGraphBuilder.addPinNavGraph(
    onBack: () -> Unit
) {
    composable(route = AddPinRoute.route) {
        AddPinScreen(
            onBack = onBack
        )
    }
}

object AddPinRoute {
    const val route = "add_pin"
}
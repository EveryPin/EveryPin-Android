package everypin.app.feature.addpin

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavController.navigateAddPin() {
    navigate(AddPinRoute.route)
}

fun NavGraphBuilder.addPinNavGraph() {
    composable(route = AddPinRoute.route) {
        AddPinScreen()
    }
}

object AddPinRoute {
    const val route = "add_pin"
}
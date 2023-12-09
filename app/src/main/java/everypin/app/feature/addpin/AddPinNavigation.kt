package everypin.app.feature.addpin

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavController.navigateAddPin() {
    navigate(AddPinRoute.route)
}

fun NavGraphBuilder.addPinNavGraph(
    onPop: () -> Unit
) {
    composable(route = AddPinRoute.route) {
        AddPinRoute(
            onPop = onPop
        )
    }
}

object AddPinRoute {
    const val route = "add_pin"
}
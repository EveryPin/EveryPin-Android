package everypin.app.feature.notification

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object NotificationRoute

fun NavController.navigateToNotification() {
    navigate(NotificationRoute)
}

fun NavGraphBuilder.notificationGraph(navController: NavController) {
    composable<NotificationRoute> {
        NotificationScreen(
            onBack = {
                navController.popBackStack()
            }
        )
    }
}
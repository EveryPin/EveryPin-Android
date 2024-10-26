package everypin.app.feature.notification

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import everypin.app.core.ui.animation.animateComposable
import kotlinx.serialization.Serializable

@Serializable
object NotificationRoute

fun NavController.navigateToNotification() {
    navigate(NotificationRoute)
}

fun NavGraphBuilder.notificationGraph(navController: NavController) {
    animateComposable<NotificationRoute> {
        NotificationScreen(
            onBack = {
                navController.popBackStack()
            }
        )
    }
}
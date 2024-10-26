package everypin.app.feature.chat

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import everypin.app.core.ui.animation.animateComposable
import everypin.app.feature.chat.view.ChatListScreen
import everypin.app.feature.chat.view.ChatRoomScreen
import everypin.app.feature.chat.view.ChatSearchScreen
import kotlinx.serialization.Serializable

@Serializable
object ChatNavigation

@Serializable
object ChatListRoute

@Serializable
object ChatSearchRoute

@Serializable
object ChatRoomRoute

fun NavController.navigateToChatList() {
    navigate(ChatListRoute)
}

fun NavController.navigateToChatSearch() {
    navigate(ChatSearchRoute)
}

fun NavController.navigateToChatRoom() {
    navigate(ChatRoomRoute)
}

fun NavGraphBuilder.chatGraph(navController: NavController) {
    navigation<ChatNavigation>(
        startDestination = ChatListRoute
    ) {
        animateComposable<ChatListRoute> {
            ChatListScreen(
                onBack = {
                    navController.popBackStack()
                },
                onNavigateToChatSearch = {
                    navController.navigateToChatSearch()
                },
                onNavigateToChatRoom = {
                    navController.navigateToChatRoom()
                }
            )
        }
        animateComposable<ChatSearchRoute> {
            ChatSearchScreen(
                onBack = {
                    navController.popBackStack()
                },
                onNavigateToChatRoom = {
                    navController.navigateToChatRoom()
                }
            )
        }
        animateComposable<ChatRoomRoute> {
            ChatRoomScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
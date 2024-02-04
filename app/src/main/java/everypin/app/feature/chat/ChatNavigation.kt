package everypin.app.feature.chat

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import everypin.app.core.ui.animation.animateComposable
import everypin.app.feature.chat.view.ChatListScreen
import everypin.app.feature.chat.view.ChatRoomScreen
import everypin.app.feature.chat.view.ChatSearchScreen

fun NavController.navigateChatList() {
    navigate(ChatRoute.chatList())
}

fun NavController.navigateChatSearch() {
    navigate(ChatRoute.chatSearch())
}

fun NavController.navigateChatRoom() {
    navigate(ChatRoute.chatRoom())
}

fun NavGraphBuilder.chatNavGraph(
    onBack: () -> Unit,
    onNavigateToChatSearch: () -> Unit,
    onNavigateToChatRoom: () -> Unit
) {
    navigation(
        startDestination = ChatRoute.chatList(),
        route = ChatRoute.route
    ) {
        animateComposable(
            route = ChatRoute.chatList()
        ) {
            ChatListScreen(
                onBack = onBack,
                onNavigateToChatSearch = onNavigateToChatSearch,
                onNavigateToChatRoom = onNavigateToChatRoom
            )
        }
        animateComposable(
            route = ChatRoute.chatSearch()
        ) {
            ChatSearchScreen(
                onBack = onBack,
                onNavigateToChatRoom = onNavigateToChatRoom
            )
        }
        animateComposable(
            route = ChatRoute.chatRoom()
        ) {
            ChatRoomScreen(
                onBack = onBack
            )
        }
    }
}

object ChatRoute {
    const val route = "chat"

    fun chatList() = "$route/list"
    fun chatSearch() = "$route/search"
    fun chatRoom() = "$route/room"
}
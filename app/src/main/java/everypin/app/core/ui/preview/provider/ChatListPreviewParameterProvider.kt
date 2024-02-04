package everypin.app.core.ui.preview.provider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import everypin.app.core.model.ChatListModel
import java.time.LocalDateTime

class ChatListPreviewParameterProvider: PreviewParameterProvider<List<ChatListModel>> {
    override val values: Sequence<List<ChatListModel>>
        get() = sequenceOf(
            buildList {
                for (i in 0..20) {
                    add(ChatListModel(
                        id = i,
                        name = "홍길동$i",
                        profileImgUrl = "https://http.cat/404",
                        content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                        lastChatDateTime = LocalDateTime.now()
                    ))
                }
            }
        )
}
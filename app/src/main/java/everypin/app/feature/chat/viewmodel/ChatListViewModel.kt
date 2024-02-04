package everypin.app.feature.chat.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import everypin.app.core.model.ChatListModel
import everypin.app.core.ui.state.UIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(

): ViewModel() {
    private val _chatList = MutableStateFlow<UIState<List<ChatListModel>>>(UIState.Loading)
    val chatList = _chatList.asStateFlow()

    init {
        fetchChatList()
    }

    private fun fetchChatList() {
        val dummy = buildList {
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
        _chatList.value = UIState.Success(dummy)
    }
}
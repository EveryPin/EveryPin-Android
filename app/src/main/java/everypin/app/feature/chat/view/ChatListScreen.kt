package everypin.app.feature.chat.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import everypin.app.R
import everypin.app.core.ui.component.CommonAsyncImage
import everypin.app.core.ui.preview.provider.ChatListPreviewParameterProvider
import everypin.app.core.ui.state.UIState
import everypin.app.core.ui.theme.EveryPinTheme
import everypin.app.data.model.ChatListModel
import everypin.app.feature.chat.viewmodel.ChatListViewModel
import java.time.format.DateTimeFormatter

@Composable
internal fun ChatListScreen(
    chatListViewModel: ChatListViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToChatSearch: () -> Unit,
    onNavigateToChatRoom: () -> Unit
) {
    val chatList by chatListViewModel.chatList.collectAsStateWithLifecycle()

    BackHandler(onBack = onBack)

    ChatListContainer(
        onBack = onBack,
        onNavigateToChatSearch = onNavigateToChatSearch,
        onNavigateToChatRoom = onNavigateToChatRoom,
        chatList = chatList
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatListContainer(
    onBack: () -> Unit,
    onNavigateToChatSearch: () -> Unit,
    onNavigateToChatRoom: () -> Unit,
    chatList: UIState<List<ChatListModel>>
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.chat_list)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToChatSearch) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_chat_add_on),
                            contentDescription = stringResource(id = R.string.chat_search)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (chatList) {
                is UIState.Error -> {
                    Column(Modifier.align(Alignment.Center)) {
                        Text(
                            text = "채팅방 목록을 불러올 수 없습니다."
                        )
                    }
                }

                UIState.Loading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                is UIState.Success -> {
                    LazyColumn {
                        items(
                            items = chatList.data,
                            key = { it.id }
                        ) {
                            ChatListItem(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                chatListModel = it,
                                onClick = {
                                    onNavigateToChatRoom()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatListItem(
    modifier: Modifier = Modifier,
    chatListModel: ChatListModel,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(
                indication = ripple(),
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .then(modifier),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CommonAsyncImage(
            data = chatListModel.profileImgUrl,
            placeholder = painterResource(id = R.drawable.ic_face),
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = chatListModel.name,
                    style = EveryPinTheme.typography.titleMedium
                )
                Text(
                    text = chatListModel.lastChatDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    style = EveryPinTheme.typography.titleSmall
                )
            }
            Text(
                text = chatListModel.content,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                style = EveryPinTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatListScreenPreview(
    @PreviewParameter(ChatListPreviewParameterProvider::class) chatList: List<ChatListModel>
) {
    EveryPinTheme {
        ChatListContainer(
            onBack = {},
            onNavigateToChatSearch = {},
            onNavigateToChatRoom = {},
            chatList = UIState.Success(chatList)
        )
    }
}
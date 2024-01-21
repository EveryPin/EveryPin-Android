package everypin.app.feature.chat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import everypin.app.R
import everypin.app.core.ui.theme.EveryPinTheme

@Composable
internal fun ChatListScreen(
    onBack: () -> Unit,
    onNavigateToChatSearch: () -> Unit,
    onNavigateToChatRoom: () -> Unit
) {

    BackHandler(onBack = onBack)

    ChatListContainer(
        onBack = onBack,
        onNavigateToChatSearch = onNavigateToChatSearch,
        onNavigateToChatRoom = onNavigateToChatRoom
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatListContainer(
    onBack: () -> Unit,
    onNavigateToChatSearch: () -> Unit,
    onNavigateToChatRoom: () -> Unit
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding))
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatListScreenPreview() {
    EveryPinTheme {
        ChatListScreen(
            onBack = {},
            onNavigateToChatSearch = {},
            onNavigateToChatRoom = {}
        )
    }
}
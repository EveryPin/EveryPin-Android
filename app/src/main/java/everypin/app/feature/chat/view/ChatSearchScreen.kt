package everypin.app.feature.chat.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import everypin.app.R
import everypin.app.core.ui.theme.EveryPinTheme

@Composable
internal fun ChatSearchScreen(
    onBack: () -> Unit,
    onNavigateToChatRoom: () -> Unit
) {
    BackHandler(onBack = onBack)

    ChatSearchContainer(
        onBack = onBack,
        onNavigateToChatRoom = onNavigateToChatRoom
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatSearchContainer(
    onBack: () -> Unit,
    onNavigateToChatRoom: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.chat_search)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding))
    }
}

@PreviewScreenSizes
@Composable
private fun ChatSearchScreenPreview() {
    EveryPinTheme {
        ChatSearchContainer(
            onBack = {},
            onNavigateToChatRoom = {}
        )
    }
}
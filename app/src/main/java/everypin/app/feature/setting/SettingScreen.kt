package everypin.app.feature.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.example.compose.EveryPinTheme
import everypin.app.R
import everypin.app.core.ui.component.button.ListItemButton

@Composable
internal fun SettingScreen(
    onBack: () -> Unit
) {
    SettingContainer(
        onBack = onBack,
        onClickBlockList = {},
        onClickSignOut = {},
        onClickTerms = {},
        onClickPrivacyPolicy = {},
        onClickDeleteAccount = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingContainer(
    onBack: () -> Unit,
    onClickBlockList: () -> Unit,
    onClickSignOut: () -> Unit,
    onClickTerms: () -> Unit,
    onClickPrivacyPolicy: () -> Unit,
    onClickDeleteAccount: () -> Unit
) {
    var isCheckedPush by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.setting)
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
        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(id = R.string.push_noti_setting)
                )
            },
            trailingContent = {
                Switch(
                    checked = isCheckedPush,
                    onCheckedChange = {
                        isCheckedPush = it
                    }
                )
            }
        )
        ListItemButton(
            onClick = onClickBlockList,
            headlineContent = {
                Text(
                    text = stringResource(id = R.string.block_list)
                )
            }
        )
        ListItemButton(
            onClick = onClickSignOut,
            headlineContent = {
                Text(
                    text = stringResource(id = R.string.sign_out)
                )
            }
        )
        ListItemButton(
            onClick = onClickTerms,
            headlineContent = {
                Text(
                    text = stringResource(id = R.string.terms)
                )
            }
        )
        ListItemButton(
            onClick = onClickPrivacyPolicy,
            headlineContent = {
                Text(
                    text = stringResource(id = R.string.privacy_policy)
                )
            }
        )
        ListItemButton(
            onClick = onClickDeleteAccount,
            headlineContent = {
                Text(
                    text = stringResource(id = R.string.delete_account)
                )
            }
        )
    }
}

@PreviewScreenSizes
@Composable
fun SettingScreenPreview() {
    EveryPinTheme {
        SettingContainer(
            onBack = {},
            onClickBlockList = {},
            onClickSignOut = {},
            onClickTerms = {},
            onClickPrivacyPolicy = {},
            onClickDeleteAccount = {}
        )
    }
}
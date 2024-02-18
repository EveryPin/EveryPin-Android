package everypin.app.feature.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.example.compose.EveryPinTheme
import everypin.app.R

@Composable
internal fun SettingScreen(
    onBack: () -> Unit
) {
    SettingContainer(
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingContainer(
    onBack: () -> Unit
) {
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
    }
}

@PreviewScreenSizes
@Composable
fun SettingScreenPreview() {
    EveryPinTheme {
        SettingContainer(
            onBack = {}
        )
    }
}
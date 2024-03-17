package everypin.app.feature.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.compose.EveryPinTheme
import everypin.app.R

@Composable
internal fun NotificationScreen(
    innerPadding: PaddingValues,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    NotificationContainer(
        innerPadding = innerPadding,
        onBack = onBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationContainer(
    innerPadding: PaddingValues,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(bottom = innerPadding.calculateBottomPadding())
            .fillMaxSize()
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.notification))
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

@Preview(showBackground = true)
@Composable
private fun NotificationScreenPreview() {
    EveryPinTheme {
        NotificationContainer(
            innerPadding = PaddingValues(),
            onBack = {}
        )
    }
}
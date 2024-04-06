package everypin.app.feature.my

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import everypin.app.R
import everypin.app.core.ui.component.CommonAsyncImage
import everypin.app.core.ui.component.UserProfile
import everypin.app.core.ui.theme.EveryPinTheme

@Composable
internal fun MyScreen(
    innerPadding: PaddingValues,
    onNavigateToSetting: () -> Unit
) {
    MyContainer(
        innerPadding = innerPadding,
        onClickSetting = onNavigateToSetting,
        onClickFollower = {},
        onClickFollow = {},
        onClickCheckPin = {},
        onClickEditProfile = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyContainer(
    innerPadding: PaddingValues,
    onClickSetting: () -> Unit,
    onClickFollower: () -> Unit,
    onClickFollow: () -> Unit,
    onClickCheckPin: () -> Unit,
    onClickEditProfile: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(
                bottom = innerPadding.calculateBottomPadding()
            )
            .fillMaxSize()
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "아이디",
                    style = EveryPinTheme.typography.titleLarge
                )
            },
            actions = {
                IconButton(
                    onClick = onClickSetting
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_setting),
                        contentDescription = stringResource(
                            id = R.string.setting
                        )
                    )
                }
            }
        )
        UserProfile(
            onClickFollower = onClickFollower,
            onClickFollow = onClickFollow,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            nickname = "닉네임",
            intro = "자기소개",
            pinCnt = 34,
            followerCnt = 3244,
            followCnt = 234242
        )
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ElevatedButton(
                onClick = onClickCheckPin,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_pin_drop),
                    contentDescription = stringResource(id = R.string.check_pin)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(id = R.string.check_pin)
                )
            }
            ElevatedButton(
                onClick = onClickEditProfile,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_manage_accounts),
                    contentDescription = stringResource(id = R.string.edit_profile)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(id = R.string.edit_profile)
                )
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(30) {
                CommonAsyncImage(
                    data = "https://picsum.photos/200",
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@PreviewScreenSizes
@Composable
private fun MyScreenPreview() {
    EveryPinTheme {
        Surface {
            MyContainer(
                innerPadding = PaddingValues(),
                onClickSetting = {},
                onClickFollower = {},
                onClickFollow = {},
                onClickCheckPin = {},
                onClickEditProfile = {}
            )
        }
    }
}
package everypin.app.feature.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset

@Composable
fun MainNavBar(
    visible: Boolean,
    selectedTab: MainTab?,
    onClickTab: (MainTab) -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideIn { IntOffset(0, it.height) },
        exit = fadeOut() + slideOut { IntOffset(0, it.height) }
    ) {
        NavigationBar {
            MainTab.entries.forEach { tab ->
                NavigationBarItem(
                    selected = selectedTab == tab,
                    onClick = {
                        onClickTab(tab)
                    },
                    icon = {
                        Icon(
                            painter = painterResource(
                                id = if (selectedTab == tab) {
                                    tab.selectedIconResId
                                } else {
                                    tab.unselectedIconResId
                                }
                            ),
                            contentDescription = tab.name,
                            tint = if (selectedTab == tab) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(id = tab.labelResId),
                            color = if (selectedTab == tab) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            fontWeight = if (selectedTab == tab) {
                                FontWeight.SemiBold
                            } else {
                                FontWeight.Medium
                            }
                        )
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun MainNavBarPreview() {
    var selectedTab by remember { mutableStateOf(MainTab.HOME) }

    MaterialTheme {
        MainNavBar(
            visible = selectedTab != MainTab.ADD_PIN,
            selectedTab = selectedTab,
            onClickTab = { tab ->
                selectedTab = tab
            }
        )
    }
}
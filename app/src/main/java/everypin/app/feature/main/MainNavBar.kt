package everypin.app.feature.main

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import everypin.app.core.ui.theme.EveryPinTheme

@Composable
fun MainNavBar(
    modifier: Modifier = Modifier,
    currentDestination: NavDestination?,
    onClick: (Any) -> Unit
) {
    NavigationBar(
        modifier = modifier
    ) {
        topLevelRoutes.forEach { topLevelRoute ->
            val selected = currentDestination?.hierarchy?.any {
                it.hasRoute(topLevelRoute.route::class)
            } == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    onClick(topLevelRoute.route)
                },
                icon = {
                    Icon(
                        painter = painterResource(
                            id = if (selected) {
                                topLevelRoute.selectedIconId
                            } else {
                                topLevelRoute.unselectedIconId
                            }
                        ),
                        contentDescription = stringResource(topLevelRoute.labelId),
                        tint = if (selected) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = topLevelRoute.labelId),
                        color = if (selected) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        fontWeight = if (selected) {
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

@Preview
@Composable
private fun MainNavBarPreview() {
    var selectedTab: Any by remember { mutableStateOf(HomeRoute) }

    EveryPinTheme {
        MainNavBar(
            currentDestination = null,
            onClick = { tab ->
                selectedTab = tab
            }
        )
    }
}
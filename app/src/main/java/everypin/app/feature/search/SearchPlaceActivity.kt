package everypin.app.feature.search

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import dagger.hilt.android.AndroidEntryPoint
import everypin.app.R
import everypin.app.core.ui.theme.EveryPinTheme
import everypin.app.data.model.PlaceInfo
import kotlinx.coroutines.flow.MutableStateFlow

@AndroidEntryPoint
class SearchPlaceActivity : ComponentActivity() {

    object ExtraKey {
        const val PLACE_INFO = "PLACE_INFO"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            EveryPinTheme {
                Surface {
                    SearchPlaceScreen(
                        onClickItem = {
                            setResult(RESULT_OK, Intent().apply {
                                putExtra(ExtraKey.PLACE_INFO, it)
                            })
                            finish()
                        },
                        onClickFindCurrentLocation = {
                            // TODO: 지도에서 위치 찾기
                            Toast.makeText(applicationContext, "미구현 기능", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchPlaceScreen(
    searchPlaceViewModel: SearchPlaceViewModel = hiltViewModel(),
    onClickItem: (PlaceInfo) -> Unit,
    onClickFindCurrentLocation: () -> Unit
) {
    val placeInfoPagingItems =
        searchPlaceViewModel.placeInfoPagingDataState.collectAsLazyPagingItems()
    val searchValue by searchPlaceViewModel.searchValue.collectAsStateWithLifecycle()

    SearchPlaceContainer(
        placeInfoPagingItems = placeInfoPagingItems,
        searchValue = searchValue,
        onSearchValueChange = {
            searchPlaceViewModel.searchValue.value = it
        },
        onClickItem = onClickItem,
        onClickFindCurrentLocation = onClickFindCurrentLocation
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchPlaceContainer(
    placeInfoPagingItems: LazyPagingItems<PlaceInfo>,
    searchValue: String,
    onSearchValueChange: (String) -> Unit,
    onClickItem: (PlaceInfo) -> Unit,
    onClickFindCurrentLocation: () -> Unit,
) {
    val onBackPressDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val loadState = placeInfoPagingItems.loadState.refresh

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.search_place),
                        style = EveryPinTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBackPressDispatcher?.onBackPressed() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = searchValue,
                onValueChange = onSearchValueChange,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                label = {
                    Text(
                        text = stringResource(id = R.string.search)
                    )
                },
                suffix = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search_24),
                        contentDescription = null
                    )
                },
                singleLine = true
            )
            ElevatedButton(
                onClick = onClickFindCurrentLocation,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.find_current_location)
                )
            }
            Box(
                modifier = Modifier.weight(1f)
            ) {
                when (loadState) {
                    is LoadState.Error -> {
                        Box(
                            modifier = Modifier
                                .padding(top = 30.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.search_error)
                            )
                        }
                    }

                    LoadState.Loading -> {}
                    is LoadState.NotLoading -> {
                        if (placeInfoPagingItems.itemCount == 0 && searchValue.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 30.dp)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(id = R.string.empty_search_result)
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(
                                    count = placeInfoPagingItems.itemCount
                                ) { index ->
                                    placeInfoPagingItems[index]?.let { item ->
                                        ListItem(
                                            headlineContent = {
                                                Text(
                                                    text = item.placeName
                                                )
                                            },
                                            supportingContent = {
                                                Text(
                                                    text = item.addressName
                                                )
                                            },
                                            modifier = Modifier.clickable {
                                                onClickItem(item)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchPlaceScreenPreview() {
    val pagingItems =
        MutableStateFlow<PagingData<PlaceInfo>>(PagingData.empty()).collectAsLazyPagingItems()
    var address by remember { mutableStateOf("") }

    EveryPinTheme {
        SearchPlaceContainer(
            placeInfoPagingItems = pagingItems,
            searchValue = address,
            onSearchValueChange = { address = it },
            onClickItem = {},
            onClickFindCurrentLocation = {}
        )
    }
}
package everypin.app.feature.post

import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import everypin.app.R
import everypin.app.core.error.HttpError
import everypin.app.core.ui.component.CommonAsyncImage
import everypin.app.core.ui.component.dialog.MenuAlertDialog
import everypin.app.core.ui.state.UIState
import everypin.app.core.ui.theme.EveryPinTheme
import everypin.app.core.utils.Logger
import everypin.app.data.model.PostDetail
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Locale

@Composable
fun PostDetailScreen(
    postDetailViewModel: PostDetailViewModel,
    onBack: () -> Unit
) {
    val postDetailState by postDetailViewModel.postDetailState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val geocoder = remember {
        Geocoder(context, Locale.getDefault())
    }
    var address by remember { mutableStateOf("") }

    LaunchedEffect(postDetailState) {
        if (postDetailState is UIState.Success) {
            val postDetail = (postDetailState as UIState.Success).data

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocation(
                        postDetail.latitude,
                        postDetail.longitude,
                        1
                    ) {
                        address = it.firstOrNull()?.getAddressLine(0) ?: ""
                    }
                } else {
                    address = geocoder.getFromLocation(
                        postDetail.latitude,
                        postDetail.longitude,
                        1
                    )?.firstOrNull()?.getAddressLine(0) ?: ""
                }
            } catch (e: Exception) {
                Logger.e("주소를 가져올 수 없음", e)
            }
        }
    }

    PostDetailContainer(
        onBack = onBack,
        postDetailState = postDetailState,
        address = address
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PostDetailContainer(
    onBack: () -> Unit,
    postDetailState: UIState<PostDetail>,
    address: String
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (postDetailState is UIState.Success) {
                        Column {
                            Text(
                                text = postDetailState.data.name,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = address,
                                modifier = Modifier.clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() },
                                    onClick = {
                                        val uri = Uri.parse("geo:0,0?q=${Uri.encode(address)}")
                                        val intent = Intent(Intent.ACTION_VIEW, uri)
                                        context.startActivity(intent)
                                    }
                                ),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
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
        AnimatedContent(
            targetState = postDetailState,
            modifier = Modifier.padding(innerPadding),
            label = "postDetailStateAnim"
        ) { state ->
            when (state) {
                is UIState.Error -> {
                    val error = state.error
                    val errMsg = if (error is HttpError && error.code == 404) {
                        stringResource(R.string.post_not_found_message)
                    } else {
                        stringResource(R.string.post_load_error_message)
                    }

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errMsg,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }

                UIState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UIState.Success -> {
                    val pagerState = rememberPagerState(
                        initialPage = 0,
                        pageCount = state.data.photoUrls::size
                    )
                    var showMoreMenu by remember { mutableStateOf(false) }

                    var favorite by remember { mutableStateOf(false) }

                    if (showMoreMenu) {
                        MenuAlertDialog(
                            onDismissRequest = {
                                showMoreMenu = false
                            },
                            items = listOf(
                                "신고", "차단", "수정", "삭제"
                            ),
                            onClick = { index ->
                                // TODO: 아이템에 따라서 처리
                                showMoreMenu = false
                            }
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        NetworkImageHorizontalPager(
                            urls = state.data.photoUrls,
                            state = pagerState,
                            onClickIndicator = {
                                scope.launch {
                                    pagerState.animateScrollToPage(it)
                                }
                            },
                            modifier = Modifier.aspectRatio(4 / 3f)
                        )
                        MenuItems(
                            favorite = favorite,
                            onClickFavorite = {
                                favorite = !favorite
                            },
                            onClickChat = {
                                // TODO: 채팅으로 이동
                            },
                            onClickMore = {
                                showMoreMenu = true
                            }
                        )
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.like_count, state.data.likeCount),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = buildAnnotatedString {
                                    withLink(LinkAnnotation.Clickable(tag = "name") {
                                        // TODO: 작성자 프로필로 이동
                                    }) {
                                        withStyle(SpanStyle(fontWeight = FontWeight.W700)) {
                                            append(state.data.name)
                                        }
                                    }
                                    append(" ")
                                    append(state.data.content)
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        // TODO: 댓글 목록
                    }
                }
            }
        }
    }
}

@Composable
private fun NetworkImageHorizontalPager(
    urls: List<String>,
    state: PagerState,
    onClickIndicator: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    var showIndicator by remember { mutableStateOf(true) }
    var indicatorVisibleJob: Job? = null

    LaunchedEffect(key1 = state.isScrollInProgress) {
        if (state.isScrollInProgress) {
            indicatorVisibleJob?.cancel()
            indicatorVisibleJob = null
            showIndicator = true
        } else {
            indicatorVisibleJob = launch {
                delay(3000)
                showIndicator = false
            }
        }
    }

    Box(
        modifier = modifier
    ) {
        HorizontalPager(
            state = state,
            modifier = modifier,
            contentPadding = contentPadding
        ) { index ->
            CommonAsyncImage(
                data = urls[index],
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        AnimatedVisibility(
            visible = showIndicator && urls.size > 1,
            modifier = Modifier.align(Alignment.TopEnd),
            enter = fadeIn(
                animationSpec = spring(stiffness = Spring.StiffnessLow)
            ),
            exit = fadeOut(
                animationSpec = spring(stiffness = Spring.StiffnessLow)
            )
        ) {
            Text(
                text = "${state.currentPage + 1}/${state.pageCount}",
                modifier = Modifier
                    .padding(8.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = CircleShape
                    )
                    .padding(horizontal = 6.dp, vertical = 3.dp),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFeatureSettings = "tnum",
                    color = Color.White
                )
            )
        }
        AnimatedVisibility(
            visible = showIndicator && urls.size > 1,
            modifier = Modifier
                .padding(bottom = 12.dp)
                .align(Alignment.BottomCenter),
            enter = fadeIn(
                animationSpec = spring(stiffness = Spring.StiffnessLow)
            ),
            exit = fadeOut(
                animationSpec = spring(stiffness = Spring.StiffnessLow)
            )
        ) {
            Row {
                for (i in 0..<state.pageCount) {
                    val indicatorColor by animateColorAsState(
                        targetValue = if (state.currentPage == i) {
                            Color.White
                        } else {
                            Color.White.copy(alpha = 0.5f)
                        }, label = "indicatorColorAnim"
                    )

                    Box(
                        modifier = Modifier
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = {
                                    onClickIndicator(i)
                                }
                            )
                            .background(
                                color = indicatorColor,
                                shape = CircleShape
                            )
                            .size(6.dp)
                    )
                    if (i != state.pageCount - 1) {
                        Spacer(modifier = Modifier.width(3.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuItems(
    favorite: Boolean,
    onClickFavorite: () -> Unit,
    onClickChat: () -> Unit,
    onClickMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                IconButton(
                    onClick = onClickFavorite
                ) {
                    AnimatedContent(
                        targetState = favorite,
                        label = "favorite"
                    ) { isFavorite ->
                        Icon(
                            imageVector = if (isFavorite) {
                                Icons.Outlined.Favorite
                            } else {
                                Icons.Outlined.FavoriteBorder
                            },
                            contentDescription = null,
                            tint = if (isFavorite) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.Unspecified
                            }
                        )
                    }
                }
                IconButton(
                    onClick = onClickChat
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Send,
                        contentDescription = null
                    )
                }
            }
            IconButton(
                onClick = onClickMore
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = null
                )
            }
        }
    }
}

@Preview
@Composable
private fun PostDetailScreenPreview() {
    val fakePostDetail = PostDetail(
        id = 1,
        name = "name",
        content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        createdDate = LocalDateTime.now(),
        latitude = 1.0,
        longitude = 1.0,
        photoUrls = listOf("", "", ""),
        likeCount = 1234
    )

    EveryPinTheme {
        PostDetailContainer(
            onBack = {},
            postDetailState = UIState.Success(fakePostDetail),
            address = "서울특별시 용산구"
        )
    }
}
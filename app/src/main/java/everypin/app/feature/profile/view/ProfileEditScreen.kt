package everypin.app.feature.profile.view

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import everypin.app.R
import everypin.app.core.extension.checkDisplayId
import everypin.app.core.extension.checkName
import everypin.app.core.ui.component.CommonAsyncImage
import everypin.app.core.ui.component.dialog.ImageAddMenuDialog
import everypin.app.core.ui.shimmerBackground
import everypin.app.core.ui.theme.EveryPinTheme
import everypin.app.core.utils.FileUtil
import everypin.app.data.model.Profile
import everypin.app.feature.profile.viewmodel.ProfileEditEvent
import everypin.app.feature.profile.viewmodel.ProfileEditUiState
import everypin.app.feature.profile.viewmodel.ProfileEditViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfileEditRoute(
    profileEditViewModel: ProfileEditViewModel = hiltViewModel(),
    onShowSnackbar: suspend (String, String?) -> SnackbarResult
) {
    val profileEditUiState = profileEditViewModel.profileState.collectAsStateWithLifecycle()
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    ProfileEditScreen(
        onBack = {
            onBackPressedDispatcher?.onBackPressed()
        },
        onSave = profileEditViewModel::updateProfile,
        uiState = profileEditUiState.value,
        onShowSnackbar = {
            scope.launch {
                onShowSnackbar(it, null)
            }
        }
    )

    LaunchedEffect(Unit) {
        profileEditViewModel.event.collect { event ->
            when (event) {
                is ProfileEditEvent.ProfileUpdateFailed -> {
                    onShowSnackbar("프로필 수정 중 문제가 발생하였습니다.", null)
                }

                ProfileEditEvent.ProfileUpdateSuccess -> {
                    focusManager.clearFocus()
                    onShowSnackbar("프로필 수정이 완료되었습니다.", null)
                }
            }
        }
    }

    DisposableEffect(key1 = Unit) {
        onDispose {
            FileUtil.deleteTempDirectory(context)
        }
    }
}

@Composable
fun ProfileEditScreen(
    onBack: () -> Unit,
    onSave: (displayId: String, name: String, selfIntroduction: String, imageUrl: String?) -> Unit,
    uiState: ProfileEditUiState,
    onShowSnackbar: (message: String) -> Unit
) {
    val profileEditUiSuccessState = uiState as? ProfileEditUiState.Success
    val isProfileUpdateLoading = profileEditUiSuccessState?.profileUpdateLoading == true
    val profile = profileEditUiSuccessState?.profile
    var profileImage by remember(uiState) { mutableStateOf(profile?.photoUrl) }
    var displayId by remember(uiState) { mutableStateOf(profile?.displayId ?: "") }
    var name by remember(uiState) { mutableStateOf(profile?.name ?: "") }
    var selfIntroduction by remember(uiState) { mutableStateOf(profile?.selfIntroduction ?: "") }
    var showImageAddMenuDialog by remember { mutableStateOf(false) }

    if (showImageAddMenuDialog) {
        ImageAddMenuDialog(
            onDismissRequest = {
                showImageAddMenuDialog = false
            },
            onSelectedImages = { images ->
                showImageAddMenuDialog = false
                profileImage = images.first().toFile().absolutePath
            },
            onError = {
                showImageAddMenuDialog = false
                onShowSnackbar("사진을 가져오는 중 문제가 발생하였습니다.")
            },
            singleItem = true
        )
    }

    Column(
        modifier = Modifier
            .background(
                color = EveryPinTheme.colors.background
            )
            .fillMaxSize()
            .imePadding()
    ) {
        ProfileEditToolbar(
            onBackClick = onBack,
            onSaveClick = {
                val changedProfileImage =
                    if (profileImage == profile?.photoUrl) null else profileImage.toString()
                onSave(displayId, name, selfIntroduction, changedProfileImage)
            },
            enabledSave = !isProfileUpdateLoading &&
                    displayId.checkDisplayId() &&
                    name.checkName() &&
                    selfIntroduction.isNotBlank() &&
                    (displayId.trim() != profile?.displayId ||
                    name.trim() != profile.name ||
                    selfIntroduction.trim() != profile.selfIntroduction ||
                    profileImage != profile.photoUrl)
        )
        Spacer(Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Box(
                    modifier = Modifier.size(80.dp)
                ) {
                    if (uiState is ProfileEditUiState.Loading) {
                        Box(
                            modifier = Modifier
                                .shimmerBackground()
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    } else {
                        CommonAsyncImage(
                            data = profileImage,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                enabled = !isProfileUpdateLoading,
                                onClick = {
                                    showImageAddMenuDialog = true
                                }
                            )
                            .shadow(
                                elevation = 5.dp,
                                shape = CircleShape
                            )
                            .background(
                                color = MaterialTheme.colorScheme.background,
                                shape = CircleShape
                            )
                            .size(24.dp)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_photo_camera_24),
                            contentDescription = stringResource(R.string.select_image),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(Modifier.width(20.dp))
                Text(
                    text = profile?.email ?: "",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(Modifier.height(32.dp))
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = displayId,
                    onValueChange = {
                        displayId = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isProfileUpdateLoading,
                    label = {
                        Text(
                            text = "ID"
                        )
                    },
                    singleLine = true
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isProfileUpdateLoading,
                    label = {
                        Text(
                            text = "이름"
                        )
                    },
                    singleLine = true
                )
                OutlinedTextField(
                    value = selfIntroduction,
                    onValueChange = {
                        selfIntroduction = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 160.dp),
                    enabled = !isProfileUpdateLoading,
                    label = {
                        Text(
                            text = "한 줄 소개"
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun ProfileEditToolbar(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    enabledSave: Boolean
) {
    Row(
        modifier = Modifier
            .shadow(10.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onBackClick
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = stringResource(R.string.back)
            )
        }
        IconButton(
            onClick = onSaveClick,
            enabled = enabledSave
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_done),
                contentDescription = stringResource(R.string.back)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileEditScreenPreview() {
    EveryPinTheme {
        ProfileEditScreen(
            onBack = {},
            onSave = { _, _, _, _ -> },
            uiState = ProfileEditUiState.Success(
                Profile(
                    email = "example@example.com",
                    displayId = "example",
                    name = "example",
                    photoUrl = null,
                    selfIntroduction = "example"
                )
            ),
            onShowSnackbar = {}
        )
    }
}
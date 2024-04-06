package everypin.app.feature.addpin

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import everypin.app.R
import everypin.app.core.ui.component.CommonAsyncImage
import everypin.app.core.ui.component.bottomsheet.ImagePickerModalBottomSheet
import everypin.app.core.ui.component.dialog.PermissionAlertDialog
import everypin.app.core.ui.theme.EveryPinTheme
import everypin.app.core.ui.theme.NoRippleTheme
import everypin.app.core.utils.FileUtil
import kotlinx.coroutines.launch

@Composable
internal fun AddPinScreen(
    addPinViewModel: AddPinViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val selectedImageList by addPinViewModel.selectedImageListState.collectAsStateWithLifecycle()

    DisposableEffect(key1 = Unit) {
        onDispose {
            FileUtil.deleteTempDirectory(context)
        }
    }

    BackHandler {
        onBack()
    }

    AddPinContainer(
        onBack = onBack,
        onClickDeleteSelectedImage = addPinViewModel::removeImage,
        onClickRegPin = { content ->

        },
        onSelectedImages = addPinViewModel::addImage,
        selectedImageList = selectedImageList
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddPinContainer(
    onBack: () -> Unit,
    onClickDeleteSelectedImage: (index: Int) -> Unit,
    onClickRegPin: (content: String) -> Unit,
    onSelectedImages: (image: Array<Uri>) -> Unit,
    selectedImageList: List<Uri>
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showImagePicker by remember { mutableStateOf(false) }
    var showPermissionGuideDialog by remember { mutableStateOf(false) }
    val imagePickerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val requestPermissions =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantedMap ->
            val isDenied = grantedMap.values.any { !it }
            if (isDenied) {
                showPermissionGuideDialog = true
            } else {
                showImagePicker = true
            }
        }
    var content by remember { mutableStateOf("") }

    if (showPermissionGuideDialog) {
        PermissionAlertDialog(
            onDismiss = {
                showPermissionGuideDialog = false
            },
            permissions = listOf(
                stringResource(id = R.string.photo_and_video),
                stringResource(id = R.string.camera)
            )
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.new_post)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = stringResource(
                                id = R.string.close
                            )
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            onClickRegPin(content)
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_done),
                            contentDescription = stringResource(
                                id = R.string.reg_pin
                            )
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (showImagePicker) {
            ImagePickerModalBottomSheet(
                onDismiss = {
                    showImagePicker = false
                },
                onConfirm = { images ->
                    scope.launch { imagePickerSheetState.hide() }.invokeOnCompletion {
                        if (!imagePickerSheetState.isVisible) {
                            showImagePicker = false
                        }
                    }
                    onSelectedImages(images.toTypedArray())
                },
                onTakePicture = {
                    onSelectedImages(arrayOf(it))
                },
                sheetState = imagePickerSheetState
            )
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .imePadding()
                .fillMaxSize()
        ) {
            ImageLazyRow(
                onClickImportImage = {
                    checkPermission(
                        context = context,
                        launcher = requestPermissions,
                        onGranted = {
                            showImagePicker = true
                        }
                    )
                },
                selectedImageList = selectedImageList,
                onClickDeleteSelectedImage = onClickDeleteSelectedImage
            )
            TextField(
                value = content,
                onValueChange = {
                    content = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                textStyle = EveryPinTheme.typography.titleMedium,
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.hint_content),
                        style = EveryPinTheme.typography.titleMedium.copy(
                            color = Color.Gray
                        )
                    )
                },
                colors = TextFieldDefaults.colors(
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImageLazyRow(
    onClickImportImage: () -> Unit,
    selectedImageList: List<Uri>,
    onClickDeleteSelectedImage: (index: Int) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 5.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilledIconButton(
                onClick = onClickImportImage,
                modifier = Modifier.size(90.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_a_photo),
                    contentDescription = null
                )
            }
        }
        itemsIndexed(selectedImageList) { index, uri ->
            Box {
                CommonAsyncImage(
                    data = uri,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                CompositionLocalProvider(
                    LocalMinimumInteractiveComponentEnforcement provides false,
                    LocalRippleTheme provides NoRippleTheme
                ) {
                    IconButton(
                        onClick = {
                            onClickDeleteSelectedImage(index)
                        },
                        modifier = Modifier
                            .padding(3.dp)
                            .size(24.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_cancel),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

private fun checkPermission(
    context: Context,
    launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
    onGranted: () -> Unit
) {
    val permissions = buildList {
        add(Manifest.permission.CAMERA)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.READ_MEDIA_IMAGES)
            add(Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    val isDenied = permissions.map { permission ->
        ContextCompat.checkSelfPermission(context, permission)
    }.any {
        it == PackageManager.PERMISSION_DENIED
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED || !isDenied
    ) {
        onGranted()
    } else {
        launcher.launch(permissions.toTypedArray())
    }
}

@PreviewScreenSizes
@Composable
private fun AddPinScreenPreview() {
    EveryPinTheme {
        AddPinContainer(
            onBack = {},
            onClickDeleteSelectedImage = {},
            onClickRegPin = {},
            onSelectedImages = {},
            selectedImageList = emptyList()
        )
    }
}
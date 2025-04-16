package everypin.app.feature.addpin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
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
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import everypin.app.R
import everypin.app.core.extension.getTempImagesDir
import everypin.app.core.extension.getUriForFile
import everypin.app.core.extension.removeTempImagesDir
import everypin.app.core.ui.component.CommonAsyncImage
import everypin.app.core.ui.component.dialog.PermissionAlertDialog
import everypin.app.core.ui.theme.EveryPinTheme
import everypin.app.core.utils.FileUtil
import everypin.app.core.utils.Logger
import everypin.app.data.model.PlaceInfo
import everypin.app.feature.search.SearchPlaceActivity
import kotlinx.coroutines.flow.collectLatest
import java.io.File
import java.util.UUID

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun AddPinScreen(
    addPinViewModel: AddPinViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val selectedImageList by addPinViewModel.selectedImageListState.collectAsStateWithLifecycle()
    val pinState by addPinViewModel.pinState.collectAsStateWithLifecycle()
    var showRegPinErrorDialog by remember { mutableStateOf(false) }
    var showRegPinSuccessDialog by remember { mutableStateOf(false) }
    var regPinErrorMessage by remember { mutableStateOf("") }
    val searchPlaceLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            val placeInfo = data?.let {
                IntentCompat.getParcelableExtra(
                    it,
                    SearchPlaceActivity.ExtraKey.PLACE_INFO,
                    PlaceInfo::class.java
                )
            }
            placeInfo?.let {
                addPinViewModel.setPinState(it.addressName, it.lat, it.lng)
            }
        }

    LaunchedEffect(key1 = Unit) {
        addPinViewModel.regPinEvent.collect { event ->
            when (event) {
                is RegPinEvent.Error -> {
                    regPinErrorMessage =
                        ContextCompat.getString(context, R.string.reg_pin_error_message)
                    showRegPinErrorDialog = true
                }

                RegPinEvent.Success -> {
                    context.removeTempImagesDir()
                    showRegPinSuccessDialog = true
                }

                RegPinEvent.EmptyAddress -> {
                    regPinErrorMessage =
                        ContextCompat.getString(context, R.string.check_pin_error_message)
                    showRegPinErrorDialog = true
                }

                RegPinEvent.EmptyImage -> {
                    regPinErrorMessage =
                        ContextCompat.getString(context, R.string.add_image_message)
                    showRegPinErrorDialog = true
                }

                RegPinEvent.EmptyContent -> {
                    regPinErrorMessage =
                        ContextCompat.getString(context, R.string.write_content_message)
                    showRegPinErrorDialog = true
                }
            }
        }
    }

    DisposableEffect(key1 = Unit) {
        onDispose {
            FileUtil.deleteTempDirectory(context)
        }
    }

    if (showRegPinErrorDialog) {
        AlertDialog(
            onDismissRequest = { showRegPinErrorDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRegPinErrorDialog = false
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.ok)
                    )
                }
            },
            title = {
                Text(
                    text = stringResource(id = R.string.default_dialog_title)
                )
            },
            text = {
                Text(
                    text = regPinErrorMessage
                )
            }
        )
    }

    if (showRegPinSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showRegPinSuccessDialog = false
                onBackPressedDispatcher?.onBackPressed()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRegPinSuccessDialog = false
                        onBackPressedDispatcher?.onBackPressed()
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.ok)
                    )
                }
            },
            title = {
                Text(
                    text = stringResource(id = R.string.reg_pin_success_title)
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.reg_pin_success_message)
                )
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AddPinContainer(
            onBack = {
                onBackPressedDispatcher?.onBackPressed()
            },
            onClickDeleteSelectedImage = addPinViewModel::removeImage,
            onClickRegPin = { content ->
                addPinViewModel.regPin(content)
            },
            onSelectedImages = addPinViewModel::addImage,
            selectedImageList = selectedImageList,
            onClickAddressSearch = {
                searchPlaceLauncher.launch(Intent(context, SearchPlaceActivity::class.java))
            },
            pinAddress = pinState?.address ?: ""
        )
        if (addPinViewModel.isLoading) {
            Box(
                modifier = Modifier
                    .pointerInteropFilter { addPinViewModel.isLoading }
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddPinContainer(
    onBack: () -> Unit,
    onClickDeleteSelectedImage: (index: Int) -> Unit,
    onClickRegPin: (content: String) -> Unit,
    onSelectedImages: (image: List<Uri>) -> Unit,
    selectedImageList: List<Uri>,
    onClickAddressSearch: () -> Unit,
    pinAddress: String
) {
    val context = LocalContext.current
    var showPermissionGuideDialog by remember { mutableStateOf(false) }
    val pickMultipleMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        uris.map { uri ->
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            val bitmap = ImageDecoder.decodeBitmap(source)
            val tempDir = context.getTempImagesDir()
            val imageFileName = "${UUID.randomUUID()}.jpg"
            val pickImageFile = File(tempDir, imageFileName)
            val outputStream = pickImageFile.outputStream()
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                onSelectedImages(listOf(pickImageFile.toUri()))
            } catch (e: Exception) {
                e.message?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
                Logger.e(e.message.toString(), e)
            } finally {
                outputStream.flush()
                outputStream.close()
                bitmap.recycle()
            }
        }
    }
    var takePictureTempFile: File? by remember { mutableStateOf(null) }
    val takePictureLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                takePictureTempFile?.let { file ->
                    if (file.length() > 0L) {
                        onSelectedImages(listOf(file.toUri()))
                    }
                    takePictureTempFile == null
                }
            }
        }
    var showImageAddMenuDialog by remember { mutableStateOf(false) }
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

    if (showImageAddMenuDialog) {
        Dialog(
            onDismissRequest = {
                showImageAddMenuDialog = false
            }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                ListItem(
                    headlineContent = {
                        Text(text = stringResource(id = R.string.take_picture))
                    },
                    modifier = Modifier.clickable {
                        val tempDir = context.getTempImagesDir()
                        takePictureTempFile = File(tempDir, "${UUID.randomUUID()}.jpg")
                        takePictureTempFile?.let {
                            val tempUri = context.getUriForFile(it)
                            takePictureLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                                putExtra(MediaStore.EXTRA_OUTPUT, tempUri)
                            })
                        }
                        showImageAddMenuDialog = false
                    }
                )
                ListItem(
                    headlineContent = {
                        Text(text = stringResource(id = R.string.select_image))
                    },
                    modifier = Modifier.clickable {
                        pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        showImageAddMenuDialog = false
                    }
                )
            }
        }
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
        Column(
            modifier = Modifier
                .padding(padding)
                .imePadding()
                .fillMaxSize()
        ) {
            ImageLazyRow(
                onClickImportImage = {
                    showImageAddMenuDialog = true
                },
                selectedImageList = selectedImageList,
                onClickDeleteSelectedImage = onClickDeleteSelectedImage
            )
            ElevatedButton(
                onClick = onClickAddressSearch,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(text = stringResource(id = R.string.search_address))
            }
            Text(
                text = stringResource(id = R.string.pin_location_for_address, pinAddress),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .fillMaxWidth(),
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
                    LocalMinimumInteractiveComponentSize provides Dp.Unspecified,
                    LocalRippleConfiguration provides RippleConfiguration(
                        rippleAlpha = RippleAlpha(
                            0f,
                            0f,
                            0f,
                            0f
                        )
                    )
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

@Preview(showBackground = true)
@Composable
private fun AddPinScreenPreview() {
    EveryPinTheme {
        AddPinContainer(
            onBack = {},
            onClickDeleteSelectedImage = {},
            onClickRegPin = {},
            onSelectedImages = {},
            selectedImageList = emptyList(),
            onClickAddressSearch = {},
            pinAddress = "서울특별시 중구 세종대로 110"
        )
    }
}
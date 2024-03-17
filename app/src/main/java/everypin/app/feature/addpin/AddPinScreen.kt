package everypin.app.feature.addpin

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.compose.EveryPinTheme
import everypin.app.R
import everypin.app.core.extension.getExternalStorageImageList
import everypin.app.core.ui.component.dialog.PermissionAlertDialog
import everypin.app.core.ui.theme.NoRippleTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddPinScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val imagePickerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showImagePicker by remember { mutableStateOf(false) }
    var showPermissionGuideDialog by remember { mutableStateOf(false) }
    val requestPermissions =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantedMap ->
            val isDenied = grantedMap.values.any { !it }
            if (isDenied) {
                showPermissionGuideDialog = true
            } else {
                showImagePicker = true
            }
        }
    val takePhotoLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.let {
                val bitmap = IntentCompat.getParcelableExtra(it, "data", Bitmap::class.java)
                val everyPinDir = context.getExternalFilesDir("images")
                if (everyPinDir?.exists() == false) {
                    everyPinDir.mkdir()
                }
                val takePhotoFile = File(everyPinDir, "EveryPin_${LocalDateTime.now()}.jpg")
                takePhotoFile.createNewFile()
                val outputStream = FileOutputStream(takePhotoFile)
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.close()
//                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", )
            }
        }

    if (showPermissionGuideDialog) {
        PermissionAlertDialog(
            onDismiss = {
                showPermissionGuideDialog = false
            },
            permissions = listOf("사진 및 동영상", "카메라")
        )
    }

    if (showImagePicker) {
        ImagePickerModalBottomSheet(
            onDismiss = {
                showImagePicker = false
            },
            onClickTakePhoto = {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePhotoLauncher.launch(intent)
            },
            onConfirm = {
                coroutineScope.launch { imagePickerSheetState.hide() }.invokeOnCompletion {
                    if (!imagePickerSheetState.isVisible) {
                        showImagePicker = false
                    }
                }
            },
            sheetState = imagePickerSheetState
        )
    }

    AddPinContainer(
        onBack = onBack,
        onClickAddPhoto = {
            checkPermission(
                context = context,
                launcher = requestPermissions,
                onGranted = {
                    showImagePicker = true
                }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePickerModalBottomSheet(
    onDismiss: () -> Unit,
    onClickTakePhoto: () -> Unit,
    onConfirm: (uris: List<Uri>) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    val context = LocalContext.current
    val imageUriList = remember { mutableStateListOf<Uri>() }
    val selectedImageUriList = remember { mutableStateListOf<Uri>() }

    LaunchedEffect(key1 = Unit) {
        imageUriList.addAll(context.getExternalStorageImageList())
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onClickTakePhoto,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "사진 촬영"
                )
            }
            Button(
                onClick = {
                    onConfirm(selectedImageUriList)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "선택 완료"
                )
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(imageUriList) { uri ->
                ImageItem(
                    uri = uri,
                    checked = selectedImageUriList.contains(uri),
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            selectedImageUriList.add(uri)
                        } else {
                            selectedImageUriList.remove(uri)
                        }
                    },
                    modifier = Modifier.aspectRatio(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageItem(
    uri: Uri,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = {
                    onCheckedChange(!checked)
                }
            )
            .then(modifier)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(uri)
                .crossfade(true)
                .dispatcher(Dispatchers.IO)
                .memoryCachePolicy(CachePolicy.DISABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .diskCacheKey(uri.toString())
                .build(),
            contentDescription = null,
            modifier = Modifier
                .border(
                    width = 3.dp,
                    color = if (checked) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.Transparent
                    }
                )
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        CompositionLocalProvider(
            LocalMinimumInteractiveComponentEnforcement provides false,
            LocalRippleTheme provides NoRippleTheme
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(5.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddPinContainer(
    onBack: () -> Unit,
    onClickAddPhoto: () -> Unit,
) {
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
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                    vertical = 5.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item {
                    FilledIconButton(
                        onClick = onClickAddPhoto,
                        modifier = Modifier.size(60.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add_a_photo),
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
            onClickAddPhoto = {}
        )
    }
}
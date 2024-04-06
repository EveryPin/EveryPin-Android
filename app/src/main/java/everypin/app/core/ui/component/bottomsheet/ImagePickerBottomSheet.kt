package everypin.app.core.ui.component.bottomsheet

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.IntentCompat
import androidx.core.net.toUri
import everypin.app.R
import everypin.app.core.extension.getExternalStorageImageList
import everypin.app.core.ui.component.CheckImageItem
import everypin.app.core.ui.theme.EveryPinTheme
import everypin.app.core.utils.FileUtil
import everypin.app.core.utils.Logger
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePickerModalBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: (uris: List<Uri>) -> Unit,
    onTakePicture: (uri: Uri) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(),
) {
    val context = LocalContext.current
    val imageUriList = remember { mutableStateListOf<Uri>() }
    val takePictureLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.let { intent ->
                var outputStream: FileOutputStream? = null
                try {
                    val bitmap = IntentCompat.getParcelableExtra(intent, "data", Bitmap::class.java)
                    val tempDir = FileUtil.getTempDirectory(context)
                    val imageFileName = "${UUID.randomUUID()}.jpg"
                    val takePhotoFile = File(tempDir, imageFileName)
                    outputStream = FileOutputStream(takePhotoFile)
                    bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    onTakePicture(takePhotoFile.toUri())
                    onDismiss()
                } catch (e: Exception) {
                    e.message?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                    Logger.e(e.message.toString(), e)
                } finally {
                    outputStream?.flush()
                    outputStream?.close()
                }
            }
        }

    LaunchedEffect(key1 = Unit) {
        imageUriList.addAll(context.getExternalStorageImageList())
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        ImagePickerBottomSheetContainer(
            onClickTakePicture = {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePictureLauncher.launch(intent)
            },
            onSelectComplete = onConfirm,
            localImageList = imageUriList
        )
    }
}

@Composable
private fun ImagePickerBottomSheetContainer(
    onClickTakePicture: () -> Unit,
    onSelectComplete: (images: List<Uri>) -> Unit,
    localImageList: List<Uri>
) {
    val selectedImageUriList = remember { mutableStateListOf<Uri>() }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onClickTakePicture,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(id = R.string.take_picture)
                )
            }
            Button(
                onClick = {
                    onSelectComplete(selectedImageUriList)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(id = R.string.select_complete)
                )
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.padding(top = 6.dp),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 6.dp,
                bottom = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(localImageList) { uri ->
                CheckImageItem(
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

@Preview
@Composable
private fun ImagePickerBottomSheetPreview() {
    EveryPinTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ImagePickerBottomSheetContainer(
                onClickTakePicture = {},
                onSelectComplete = {},
                localImageList = emptyList()
            )
        }
    }
}
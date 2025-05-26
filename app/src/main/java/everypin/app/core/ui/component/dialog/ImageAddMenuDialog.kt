package everypin.app.core.ui.component.dialog

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import everypin.app.R
import everypin.app.core.extension.getTempImagesDir
import everypin.app.core.extension.getUriForFile
import everypin.app.core.utils.CommonUtil
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import java.io.File
import java.util.UUID

@Composable
fun ImageAddMenuDialog(
    onDismissRequest: () -> Unit,
    onSelectedImages: (image: List<Uri>) -> Unit,
    onError: (Throwable) -> Unit,
    singleItem: Boolean = false
) {
    val context = LocalContext.current
    val pickMediaLauncher = if (singleItem) {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            val compressImageUri = uri?.let {
                CommonUtil.createTempImage(context, it).onFailure(onError).getOrNull()
            }
            compressImageUri?.let {
                onSelectedImages(listOf(it))
            }
        }
    } else {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia()
        ) { uris ->
            val compressImageUris = uris.mapNotNull { uri ->
                CommonUtil.createTempImage(context, uri).onFailure {
                    logcat("ImageAddMenuDialog", LogPriority.ERROR) { it.asLog() }
                }.getOrNull()
            }
            onSelectedImages(compressImageUris)
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

    Dialog(
        onDismissRequest = onDismissRequest
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
                }
            )
            ListItem(
                headlineContent = {
                    Text(text = stringResource(id = R.string.select_image))
                },
                modifier = Modifier.clickable {
                    pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            )
        }
    }
}
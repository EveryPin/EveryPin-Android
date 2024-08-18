package everypin.app.core.extension

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import androidx.core.content.FileProvider
import everypin.app.BuildConfig
import java.io.File

fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Not found activity")
}

fun Context.getUriForFile(file: File): Uri = FileProvider.getUriForFile(
    this,
    "${BuildConfig.APPLICATION_ID}.EveryPinFileProvider",
    file
)

fun Context.getTempImagesDir(): File {
    val file = File(filesDir, "temp/images")
    if (!file.exists()) {
        file.mkdirs()
    }
    return file
}

fun Context.removeTempImagesDir() {
    val file = getTempImagesDir()
    if (file.exists()) {
        file.deleteRecursively()
    }
}
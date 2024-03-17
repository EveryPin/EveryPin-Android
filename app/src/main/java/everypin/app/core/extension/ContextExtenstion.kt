package everypin.app.core.extension

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Not found activity")
}

fun Context.getExternalStorageImageList(): List<Uri> {
    val itemList = mutableListOf<Uri>()
    val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(
        MediaStore.MediaColumns._ID,
        MediaStore.MediaColumns.DATA,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        MediaStore.MediaColumns.DATE_ADDED
    )
    val selection =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.Images.Media.SIZE + " > 0"
        else null
    val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"
    val cursor = contentResolver.query(uri, projection, selection, null, sortOrder)
    cursor?.let {
        while (cursor.moveToNext()) {
            val columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
            if (columnIndex > -1) {
                val mediaPath = cursor.getString(columnIndex)
                itemList.add(Uri.parse(mediaPath))
            }
        }
    }
    cursor?.close()
    return itemList
}
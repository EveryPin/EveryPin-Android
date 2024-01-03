package everypin.app.core.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import everypin.app.core.extension.findActivity

fun checkPermissionGranted(context: Context, permission: String): Boolean =
    ContextCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED

fun checkMultiplePermissionGranted(context: Context, permissions: Array<String>): Boolean =
    permissions.none { !checkPermissionGranted(context, it) }

fun requestMultiplePermission(
    context: Context,
    permissions: Array<String>,
    launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
    onGranted: () -> Unit,
    onShowRequestPermissionRationale: () -> Unit
) {
    when {
        checkMultiplePermissionGranted(context, permissions) -> {
            onGranted()
        }

        permissions.none {
            !ActivityCompat.shouldShowRequestPermissionRationale(
                context.findActivity(), it
            )
        } -> {
            onShowRequestPermissionRationale()
        }

        else -> {
            launcher.launch(permissions)
        }
    }
}
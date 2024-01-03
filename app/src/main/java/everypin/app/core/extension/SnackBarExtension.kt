package everypin.app.core.extension

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.core.content.ContextCompat
import everypin.app.R

suspend fun SnackbarHostState.showSnackBarForPermissionSetting(
    context: Context,
    message: String
) {
    val result = showSnackbar(
        message = message,
        actionLabel = ContextCompat.getString(context, R.string.setting),
        withDismissAction = true,
        duration = SnackbarDuration.Short
    )

    if (result == SnackbarResult.ActionPerformed) {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }.run {
            context.startActivity(this)
        }
    }
}
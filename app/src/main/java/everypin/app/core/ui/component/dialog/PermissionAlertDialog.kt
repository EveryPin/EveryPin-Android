package everypin.app.core.ui.component.dialog

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import everypin.app.core.ui.theme.EveryPinTheme

/**
 * @param onDismiss 다이얼로그 닫기 동작
 *
 * 예) showDialog = false
 *
 * @param permissions 안내 할 권한 이름
 *
 * 예) listOf("사진 및 동영상", "위치")
 */
@Composable
fun PermissionAlertDialog(
    onDismiss: () -> Unit,
    permissions: List<String>
) {
    val context = LocalContext.current

    val text = buildAnnotatedString {
        append("해당 기능을 이용하기 위해서 다음 권한이 필요해요.")
        append("\n\n")
        permissions.forEach {
            append(" · $it\n")
        }
        append("\n")
        append("설정으로 이동하여 권한을 허용하시겠어요?")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "접근 권한 안내",
                style = EveryPinTheme.typography.titleMedium
            )
        },
        text = {
            Text(
                text = text
            )
        },
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }.run {
                    context.startActivity(this)
                }
            }) {
                Text(
                    text = "예",
                    style = EveryPinTheme.typography.labelMedium
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "아니오",
                    style = EveryPinTheme.typography.labelMedium
                )
            }
        }
    )
}
package everypin.app.core.ui.component

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import everypin.app.core.ui.theme.NoRippleTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckImageItem(
    uri: Uri,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
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
        CommonAsyncImage(
            data = uri,
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
package everypin.app.core.ui.component.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuAlertDialog(
    onDismissRequest: () -> Unit,
    items: List<String>,
    onClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .then(modifier)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            for (i in items.indices) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = items[i],
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                textAlign = TextAlign.Center
                            )
                        )
                    },
                    modifier = Modifier
                        .clickable {
                            onClick(i)
                        }
                        .fillMaxWidth()
                )
                if (i != items.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 1.dp,
                        color = Color(0xFFF1F1F1)
                    )
                }
            }
        }
    }
}
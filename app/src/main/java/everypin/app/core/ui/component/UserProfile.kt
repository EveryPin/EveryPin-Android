package everypin.app.core.ui.component

import android.icu.text.DecimalFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import everypin.app.R

@Composable
fun UserProfile(
    onClickFollower: () -> Unit,
    onClickFollow: () -> Unit,
    modifier: Modifier = Modifier,
    nickname: String,
    intro: String,
    pinCnt: Int,
    followerCnt: Int,
    followCnt: Int
) {
    val formatter = DecimalFormat("#,###.##")

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://picsum.photos/200")
                .crossfade(true)
                .build(),
            placeholder = painterResource(id = R.drawable.ic_face),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = nickname,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = intro,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (pinCnt >= 10000) {
                            "${formatter.format(pinCnt / 1000 * 0.1)}만"
                        } else {
                            formatter.format(pinCnt)
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "게시 핀",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Column(
                    modifier = Modifier
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = onClickFollower
                        )
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (followerCnt >= 10000) {
                            "${formatter.format(followerCnt / 1000 * 0.1)}만"
                        } else {
                            formatter.format(followerCnt)
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "팔로워",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Column(
                    modifier = Modifier
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = onClickFollow
                        )
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (followCnt >= 10000) {
                            "${formatter.format(followCnt / 1000 * 0.1)}만"
                        } else {
                            formatter.format(followCnt)
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "팔로우",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
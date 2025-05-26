package everypin.app.core.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import everypin.app.R
import everypin.app.core.ui.shimmerBackground

@Composable
fun CommonAsyncImage(
    data: Any?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    crossFade: Boolean = true,
    contentScale: ContentScale = ContentScale.Fit
) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(data ?: R.drawable.img_placeholder)
            .crossfade(crossFade)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        loading = {
            Box(
                modifier = Modifier
                    .shimmerBackground()
                    .then(modifier)
            )
        },
        error = {
            Image(
                painter = painterResource(R.drawable.img_image_load_error),
                contentDescription = null,
                modifier = modifier,
                contentScale = ContentScale.Crop
            )
        },
        contentScale = contentScale,
    )
}
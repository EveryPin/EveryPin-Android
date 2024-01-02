package everypin.app.feature.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
import everypin.app.core.utils.checkPermissionGranted

@Composable
internal fun HomeScreen(
    padding: PaddingValues
) {
    HomeContainer(padding = padding)
}

@Composable
private fun HomeContainer(
    padding: PaddingValues
) {
    val context = LocalContext.current
    var isLocationPermissionGranted by remember {
        mutableStateOf(
            checkPermissionGranted(
                context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
    val seoul = LatLng(37.5668, 126.9783)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(seoul, 14f)
    }

    GoogleMap(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = isLocationPermissionGranted
        ),
        onMyLocationButtonClick = {
            checkPermissionGranted(
                context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ).also {
                isLocationPermissionGranted = it
            }
        }
    ) {

    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    MaterialTheme {
        HomeContainer(padding = PaddingValues())
    }
}
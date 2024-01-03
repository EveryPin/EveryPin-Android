package everypin.app.feature.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import everypin.app.R
import everypin.app.core.extension.findActivity
import everypin.app.core.extension.showSnackBarForPermissionSetting
import kotlinx.coroutines.launch

@Composable
internal fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    padding: PaddingValues
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val snackBarHostState = remember { SnackbarHostState() }
    var isLocationPermissionGranted by remember {
        mutableStateOf(
            locationPermissions.any {
                ContextCompat.checkSelfPermission(
                    context,
                    it
                ) == PackageManager.PERMISSION_GRANTED
            }
        )
    }
    val locationPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionMap ->
            if (permissionMap[Manifest.permission.ACCESS_COARSE_LOCATION] != true) {
                coroutineScope.launch {
                    snackBarHostState.showSnackBarForPermissionSetting(
                        context,
                        ContextCompat.getString(context, R.string.location_permission_guide)
                    )
                }
                return@rememberLauncherForActivityResult
            }

            if (permissionMap[Manifest.permission.ACCESS_FINE_LOCATION] != true) {
                coroutineScope.launch {
                    snackBarHostState.showSnackBarForPermissionSetting(
                        context,
                        ContextCompat.getString(context, R.string.fine_location_permission_guide)
                    )
                }
                return@rememberLauncherForActivityResult
            }

            isLocationPermissionGranted = true
        }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(37.5668, 126.9783), 15f)
    }
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        if (locationPermissions.none {
                ContextCompat.checkSelfPermission(
                    context,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            }) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                coroutineScope.launch {
                    cameraPositionState.move(
                        CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 15f)
                    )
                }
            }
        }
    }

    HomeContainer(
        padding = padding,
        isLocationPermissionGranted = isLocationPermissionGranted,
        snackBarHostState = snackBarHostState,
        cameraPositionState = cameraPositionState,
        onClickLocationButton = {
            when {
                locationPermissions.none {
                    ContextCompat.checkSelfPermission(
                        context,
                        it
                    ) != PackageManager.PERMISSION_GRANTED
                } -> {
                    isLocationPermissionGranted = true
                    fusedLocationClient.lastLocation.addOnSuccessListener {
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLng(LatLng(it.latitude, it.longitude))
                            )
                        }
                    }
                }

                ActivityCompat.shouldShowRequestPermissionRationale(
                    context.findActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
                ) -> {
                    coroutineScope.launch {
                        snackBarHostState.showSnackBarForPermissionSetting(
                            context,
                            ContextCompat.getString(context, R.string.location_permission_guide)
                        )
                    }
                }

                ActivityCompat.shouldShowRequestPermissionRationale(
                    context.findActivity(), Manifest.permission.ACCESS_FINE_LOCATION
                ) -> {
                    coroutineScope.launch {
                        snackBarHostState.showSnackBarForPermissionSetting(
                            context,
                            ContextCompat.getString(
                                context,
                                R.string.fine_location_permission_guide
                            )
                        )
                    }

                    isLocationPermissionGranted = true
                    fusedLocationClient.lastLocation.addOnSuccessListener {
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLng(LatLng(it.latitude, it.longitude))
                            )
                        }
                    }
                }

                else -> {
                    locationPermissionLauncher.launch(locationPermissions)
                }
            }
        }
    )
}

@Composable
private fun HomeContainer(
    padding: PaddingValues,
    isLocationPermissionGranted: Boolean,
    snackBarHostState: SnackbarHostState,
    cameraPositionState: CameraPositionState,
    onClickLocationButton: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = isLocationPermissionGranted
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = false,
                rotationGesturesEnabled = false,
                zoomControlsEnabled = false
            )
        ) {

        }
        Box(
            modifier = Modifier
                .padding(bottom = 20.dp, end = 10.dp)
                .align(Alignment.BottomEnd)
                .background(
                    color = Color.White.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(3.dp)
                )
                .clip(RoundedCornerShape(3.dp))
                .clickable(
                    indication = rememberRipple(),
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onClickLocationButton
                )
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_my_location),
                contentDescription = null
            )
        }
        SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    MaterialTheme {
        HomeContainer(
            padding = PaddingValues(),
            isLocationPermissionGranted = true,
            snackBarHostState = SnackbarHostState(),
            cameraPositionState = rememberCameraPositionState(),
            onClickLocationButton = {}
        )
    }
}
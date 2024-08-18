package everypin.app.feature.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import everypin.app.R
import everypin.app.core.extension.findActivity
import everypin.app.core.extension.showSnackBarForPermissionSetting
import everypin.app.core.ui.theme.EveryPinTheme
import everypin.app.data.model.PostModel
import kotlinx.coroutines.launch

@Composable
internal fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    innerPadding: PaddingValues,
    onNavigateToNotification: () -> Unit,
    onNavigateToChatList: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
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
                scope.launch {
                    snackBarHostState.showSnackBarForPermissionSetting(
                        context,
                        ContextCompat.getString(context, R.string.location_permission_guide)
                    )
                }
                return@rememberLauncherForActivityResult
            }

            if (permissionMap[Manifest.permission.ACCESS_FINE_LOCATION] != true) {
                scope.launch {
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
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val postListState by homeViewModel.postListState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        if (isLocationPermissionGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                val latLng = location?.let {
                    LatLng(it.latitude, it.longitude)
                }
                val cameraUpdate = latLng?.let {
                    CameraUpdateFactory.newLatLngZoom(it, 15f)
                }
                scope.launch {
                    cameraUpdate?.let {
                        cameraPositionState.move(it)
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = cameraPositionState.position) {
        if (!cameraPositionState.isMoving) {
            homeViewModel.fetchPostList()
        }
    }

    HomeContainer(
        innerPadding = innerPadding,
        isLocationPermissionGranted = isLocationPermissionGranted,
        snackBarHostState = snackBarHostState,
        postListState = postListState,
        cameraPositionState = cameraPositionState,
        onClickLocationButton = {
            isLocationPermissionGranted = locationPermissions.any {
                ContextCompat.checkSelfPermission(
                    context,
                    it
                ) == PackageManager.PERMISSION_GRANTED
            }

            when {
                isLocationPermissionGranted -> {
                    fusedLocationClient.lastLocation.addOnSuccessListener {
                        scope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLng(LatLng(it.latitude, it.longitude))
                            )
                        }
                    }

                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            context.findActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        scope.launch {
                            snackBarHostState.showSnackBarForPermissionSetting(
                                context,
                                ContextCompat.getString(
                                    context,
                                    R.string.fine_location_permission_guide
                                )
                            )
                        }
                    }
                }

                ActivityCompat.shouldShowRequestPermissionRationale(
                    context.findActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
                ) -> {
                    scope.launch {
                        snackBarHostState.showSnackBarForPermissionSetting(
                            context,
                            ContextCompat.getString(context, R.string.location_permission_guide)
                        )
                    }
                }

                else -> {
                    locationPermissionLauncher.launch(locationPermissions)
                }
            }
        },
        onClickNotification = onNavigateToNotification,
        onClickChat = onNavigateToChatList
    )
}

@Composable
private fun HomeContainer(
    innerPadding: PaddingValues,
    isLocationPermissionGranted: Boolean,
    snackBarHostState: SnackbarHostState,
    postListState: List<PostModel>,
    cameraPositionState: CameraPositionState,
    onClickLocationButton: () -> Unit,
    onClickNotification: () -> Unit,
    onClickChat: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(bottom = innerPadding.calculateBottomPadding())
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            HomeTopAppBar(
                onClickNotification = onClickNotification,
                onClickChat = onClickChat
            )
            GoogleMap(
                modifier = Modifier.weight(1f),
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
                postListState.forEach { post ->
                    Marker(
                        state = MarkerState(LatLng(post.latitude, post.longitude)),
                        title = post.content,
                        snippet = post.createdDate.toString()
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .padding(bottom = 20.dp, end = 10.dp)
                .align(Alignment.BottomEnd)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar(
    onClickNotification: () -> Unit,
    onClickChat: () -> Unit
) {
    TopAppBar(
        title = {
            Text(stringResource(id = R.string.app_name))
        },
        actions = {
            IconButton(
                onClick = onClickNotification
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_notifications),
                    contentDescription = stringResource(id = R.string.notification),
                )
            }
            IconButton(
                onClick = onClickChat
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chat),
                    contentDescription = stringResource(id = R.string.chat),
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    EveryPinTheme {
        HomeContainer(
            innerPadding = PaddingValues(),
            isLocationPermissionGranted = true,
            snackBarHostState = SnackbarHostState(),
            postListState = emptyList(),
            cameraPositionState = rememberCameraPositionState(),
            onClickLocationButton = {},
            onClickNotification = {},
            onClickChat = {}
        )
    }
}
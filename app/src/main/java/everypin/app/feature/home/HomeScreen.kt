package everypin.app.feature.home

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
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
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap.OnCameraIdleListener
import com.naver.maps.map.compose.DisposableMapEffect
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationOverlay
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberMarkerState
import everypin.app.R
import everypin.app.core.extension.findActivity
import everypin.app.core.extension.showSnackBarForPermissionSetting
import everypin.app.core.ui.theme.EveryPinTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalNaverMapApi::class)
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
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val postListState by homeViewModel.postListState.collectAsStateWithLifecycle()
    val cameraPositionState = rememberCameraPositionState()
    var currentLocationLatLng: LatLng? by remember {
        mutableStateOf(null)
    }

    LaunchedEffect(key1 = Unit) {
        if (isLocationPermissionGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                currentLocationLatLng = location?.let {
                    LatLng(it.latitude, it.longitude)
                }
                val cameraUpdate = currentLocationLatLng?.let {
                    CameraUpdate.scrollTo(it).animate(CameraAnimation.Easing)
                }
                cameraUpdate?.let {
                    scope.launch {
                        cameraPositionState.animate(it)
                    }
                }
            }
        }
    }

    HomeContainer(
        innerPadding = innerPadding,
        snackBarHostState = snackBarHostState,
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
                            val latLng = LatLng(it.latitude, it.longitude)
                            val cameraUpdate =
                                CameraUpdate.scrollTo(latLng)
                                    .animate(CameraAnimation.Easing)
                            currentLocationLatLng = latLng
                            cameraPositionState.animate(cameraUpdate)
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
        onClickChat = onNavigateToChatList,
        mapContent = {
            NaverMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    isRotateGesturesEnabled = false,
                    isCompassEnabled = false,
                    isZoomControlEnabled = false
                )
            ) {
                DisposableMapEffect(key1 = Unit) { map ->
                    val listener = OnCameraIdleListener {
                        homeViewModel.fetchPostList()
                    }
                    map.addOnCameraIdleListener(listener)

                    onDispose {
                        map.removeOnCameraIdleListener(listener)
                    }
                }

                currentLocationLatLng?.let {
                    LocationOverlay(
                        position = it
                    )
                }

                postListState.forEach {
                    Marker(
                        state = rememberMarkerState(position = LatLng(it.latitude, it.longitude)),
                        onClick = { _ ->
                            Toast.makeText(
                                context,
                                "${it.latitude}, ${it.longitude}",
                                Toast.LENGTH_SHORT
                            ).show()
                            false
                        }
                    )
                }
            }
        }
    )
}

@Composable
private fun HomeContainer(
    innerPadding: PaddingValues,
    snackBarHostState: SnackbarHostState,
    onClickLocationButton: () -> Unit,
    onClickNotification: () -> Unit,
    onClickChat: () -> Unit,
    mapContent: @Composable () -> Unit
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
            Box(modifier = Modifier.weight(1f)) {
                mapContent()
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
            snackBarHostState = SnackbarHostState(),
            onClickLocationButton = {},
            onClickNotification = {},
            onClickChat = {},
            mapContent = {}
        )
    }
}
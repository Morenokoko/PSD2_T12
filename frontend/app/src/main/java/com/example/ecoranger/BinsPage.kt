package com.example.ecoranger

import BackHandler
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import com.example.ecoranger.data.Bin
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private const val REQUEST_LOCATION_PERMISSIONS = 123


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BinsPage(navController: NavHostController, selectedItem: MutableState<Int>) {
    val context = LocalContext.current
    val apiService = GetRecyclingCenters500mApiService.create(MainActivity.RECYCLING_CENTER_BASE_URL)
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    val exitDialogShown = remember { mutableStateOf(false) }
    val singapore = LatLng(1.35, 103.87)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 10f)
    }
    val coroutineScope = rememberCoroutineScope()
    var userPosition by remember { mutableStateOf<LatLng?>(null) }
    var binsList by remember { mutableStateOf<List<Bin>>(emptyList()) }

    // Get user's location and update the camera position
    LaunchedEffect(Unit) {
        try {
            // Check for location permissions
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request location permissions if not granted
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    REQUEST_LOCATION_PERMISSIONS
                )
                return@LaunchedEffect
            }

            // Get the last known location
            val location = fusedLocationClient.lastLocation.await()

            // Ensure that the location is not null
            location?.let {
                val userLatLng = LatLng(location.latitude, location.longitude)
                userPosition = userLatLng

                coroutineScope.launch {
                    // adjust the zoom value as needed: 0f being the farthest zoom and 21f being the closest zoom
                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(userLatLng, 17f))
                    try {
                        userPosition?.let { position ->
                            val response =
                                apiService.getRecyclingBins(position.latitude, position.longitude)
                            withContext(Dispatchers.Main) {
                                binsList = response
                                Log.d("API_SUCCESS", "Bins list retrieved successfully: $binsList")
                            }
                        }
                    } catch (e: Exception) {
                        // Handle errors
                        Log.e("API_ERROR", "Error fetching bins list: ${e.message}", e)
                    }
                }
            }
        } catch (e: Exception) {
            // Handle exceptions
        }
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        Log.d("CAMERA", "camera position changed")
        delay(1000) // delay for 1s to make sure user is not moving camera anymore

        // Check if the camera is still not moving after 2 seconds
        if (!cameraPositionState.isMoving) {
            val centerLatLng = cameraPositionState.position.target

            // Fetch bins near the new center position
            try {
                val response = apiService.getRecyclingBins(centerLatLng.latitude, centerLatLng.longitude)
                withContext(Dispatchers.Main) {
                    binsList = response
                    Log.d("API_SUCCESS", "Bins list retrieved successfully: $binsList")
                }
            } catch (e: Exception) {
                // Handle errors
                Log.e("API_ERROR", "Error fetching bins list: ${e.message}", e)
            }
        }
    }

    BackHandler(
        onBackPressed = {
            exitDialogShown.value = true
        }
    )
    if (exitDialogShown.value) {
        ExitConfirmationDialog(
            onConfirm = {
                exitApp()
            },
            onDismiss = {
                exitDialogShown.value = false
            }
        )
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController, selectedItem) },
        content = { padding -> // added padding so that maps is not hidden behind bottomBar
            Column(
                modifier = Modifier.padding(padding)
            ) {
                GoogleMap(
                    modifier = Modifier
                        .fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    userPosition?.let { position ->
                        // Update marker to be at user's position
                        Marker(
                            state = MarkerState(position = position),
                            title = "You are here",
                            snippet = "Your current location"
                        )
                    }
                    // map each bin location
                    binsList.forEach { bin ->
                        val binLatLng = LatLng(bin.latitude, bin.longitude)
                        Marker(
                            state = MarkerState(position = binLatLng),
                            title = bin.ADDRESSSTREETNAME,
                            snippet = bin.ADDRESSPOSTALCODE,
                        )
                    }
                }
            }
        }
    )
}

private fun exitApp() {
    // Close the app
    android.os.Process.killProcess(android.os.Process.myPid())
}

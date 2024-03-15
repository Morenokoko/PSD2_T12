package com.example.ecoranger

import BackHandler
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.view.PreviewView
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import android.graphics.Color
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.LifecycleOwner
import android.provider.Settings
import android.net.Uri
import android.app.Activity
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CameraPage(navController: NavHostController, selectedItem: MutableState<Int>) {
    val exitDialogShown = remember { mutableStateOf(false) }

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
        content = {
            CameraPreview()
        }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnsafeExperimentalUsageError")
@Composable
fun CameraPreview() {
//    val activity = (LocalContext.current as? Activity)
//    val camera = remember { mutableStateOf<Camera?>(null) }
    val hasDeniedTwice = remember { mutableStateOf(false) }
    val hasCameraPermission: PermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    // If camera permission is granted, start camera preview
    if (hasCameraPermission.status.isGranted) {
        CameraContent()
    } else if (!hasDeniedTwice.value) {
        PermissionNotGrantedMessage { permissionLauncher.launch(Manifest.permission.CAMERA) }
    } else {
        navigateToAppSettings()
    }

}

@Composable
private fun navigateToAppSettings() {
    val intent = Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", LocalContext.current.packageName, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    LocalContext.current.startActivity(intent)
}

@Composable
private fun CameraContent() {

    val context: Context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val cameraController: LifecycleCameraController =
        remember { LifecycleCameraController(context) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "Take photo") },
                onClick = { },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Camera,
                        contentDescription = "Camera capture icon"
                    )
                }
            )
        }
    ) { paddingValues: PaddingValues ->

        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                factory = { context ->
                    PreviewView(context).apply {
                        layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                        setBackgroundColor(Color.BLACK)
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        scaleType = PreviewView.ScaleType.FILL_START
                    }.also { previewView ->
                        previewView.controller = cameraController
                        cameraController.bindToLifecycle(lifecycleOwner)
                    }
                }
            )
        }
    }
}

@Composable
fun PermissionNotGrantedMessage(onRequestPermission: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Camera permission needed to access this feature.",
            style = TextStyle(fontSize = 16.sp, textAlign = TextAlign.Center)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRequestPermission) {
            Text("Grant Permission")
        }
    }
}


private fun exitApp() {
    // Close the app
    android.os.Process.killProcess(android.os.Process.myPid())
}

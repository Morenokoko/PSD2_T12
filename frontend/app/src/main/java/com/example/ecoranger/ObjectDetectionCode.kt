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
import android.util.Log
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import android.graphics.Color
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
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
import android.provider.Settings
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FabPosition
import androidx.compose.runtime.LaunchedEffect
import java.io.File
import androidx.core.content.ContextCompat


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ObjectDetectionPage(navController: NavHostController, selectedItem: MutableState<Int>) {
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
            CameraPreview(navController)
        }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnsafeExperimentalUsageError")
@Composable
fun CameraPreview(navController: NavHostController) {
//    val activity = (LocalContext.current as? Activity)
//    val camera = remember { mutableStateOf<Camera?>(null) }
    val hasDeniedTwice = remember { mutableStateOf(false) }
    val hasCameraPermission: PermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    // If camera permission is granted, start camera preview
    if (hasCameraPermission.status.isGranted) {
        CameraContent(navController)
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
private fun CameraContent(navController: NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    val previewView = remember { PreviewView(context) }
    val preview = Preview.Builder().build().also {
        it.setSurfaceProvider(previewView.surfaceProvider)
    }

    LaunchedEffect(cameraProviderFuture) {
        val cameraProvider = cameraProviderFuture.get()
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (exc: Exception) {
            Log.e("CameraContent", "Use case binding failed", exc)
        }
    }

    // Prepare to save captured images
    val outputDirectory = getOutputDirectory(context)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "Take photo") },
                onClick = {
                    // Capture the image and save it
                    val photoFile = File(
                        getOutputDirectory(context), "temp.jpg")
                    val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                    imageCapture.takePicture(
                        outputFileOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                Log.d("ObjectDetectionPage", "Image saved: ${photoFile.absolutePath}")
                                // Navigate to the results page or do something with the image
                                navController.navigate("resultsPage")
                            }

                            override fun onError(exception: ImageCaptureException) {
                                Log.e("ObjectDetectionPage", "Image capture failed", exception)
                            }
                        }
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Camera,
                        contentDescription = "Camera capture icon"
                    )
                },
                modifier = Modifier
                    .padding(all = 100.dp)
                    .width(150.dp)
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { paddingValues: PaddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                factory = { context ->
                    previewView.apply {
                        layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                        setBackgroundColor(Color.BLACK)
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        scaleType = PreviewView.ScaleType.FILL_START
                    }
                }
            )
        }
    }
}

fun getOutputDirectory(context: Context): File {
    val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
        File(it, context.resources.getString(R.string.app_name)).apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists())
        mediaDir else context.filesDir
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

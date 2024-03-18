package com.example.ecoranger

import BackHandler
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import android.provider.Settings
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScanQrPage(navController: NavHostController, selectedItem: MutableState<Int>) {
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

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    var code by remember { mutableStateOf("") }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(key1 = true) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { // Define the topBar content here
            Text(
                text = code,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Use the padding provided by Scaffold
        ) {
            AndroidView(
                factory = { context ->
                    val previewView = PreviewView(context)
                    val preview = Preview.Builder().build()
                    val selector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                    imageAnalysis.setAnalyzer(
                        ContextCompat.getMainExecutor(context),
                        QRCodeAnalyzer { result ->
                            result?.let {
                                code = it
                                Log.d("QRCodeAnalyzer", "Detected QR Code: $code")
                            }
                        }
                    )

                    try {
                        cameraProviderFuture.get().bindToLifecycle(
                            lifecycleOwner,
                            selector,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    previewView
                },
                modifier = Modifier.matchParentSize() // Ensures the AndroidView fills the Box
            )

            ViewFinder()
        }
    }

}

@Composable
fun ViewFinder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val boxSize = 300.dp
        val cornerLength = 50.dp
        val cornerStroke = 4.dp

        Canvas(modifier = Modifier.size(boxSize)) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Top left corner
            drawLine(
                color = Color.White,
                start = Offset(0f, 0f),
                end = Offset(cornerLength.toPx(), 0f),
                strokeWidth = cornerStroke.toPx()
            )
            drawLine(
                color = Color.White,
                start = Offset(0f, 0f),
                end = Offset(0f, cornerLength.toPx()),
                strokeWidth = cornerStroke.toPx()
            )

            // Top right corner
            drawLine(
                color = Color.White,
                start = Offset(canvasWidth - cornerLength.toPx(), 0f),
                end = Offset(canvasWidth, 0f),
                strokeWidth = cornerStroke.toPx()
            )
            drawLine(
                color = Color.White,
                start = Offset(canvasWidth, 0f),
                end = Offset(canvasWidth, cornerLength.toPx()),
                strokeWidth = cornerStroke.toPx()
            )

            // Bottom left corner
            drawLine(
                color = Color.White,
                start = Offset(0f, canvasHeight - cornerLength.toPx()),
                end = Offset(0f, canvasHeight),
                strokeWidth = cornerStroke.toPx()
            )
            drawLine(
                color = Color.White,
                start = Offset(0f, canvasHeight),
                end = Offset(cornerLength.toPx(), canvasHeight),
                strokeWidth = cornerStroke.toPx()
            )

            // Bottom right corner
            drawLine(
                color = Color.White,
                start = Offset(canvasWidth - cornerLength.toPx(), canvasHeight),
                end = Offset(canvasWidth, canvasHeight),
                strokeWidth = cornerStroke.toPx()
            )
            drawLine(
                color = Color.White,
                start = Offset(canvasWidth, canvasHeight - cornerLength.toPx()),
                end = Offset(canvasWidth, canvasHeight),
                strokeWidth = cornerStroke.toPx()
            )
        }
    }
}

private fun exitApp() {
    // Close the app
    android.os.Process.killProcess(android.os.Process.myPid())
}
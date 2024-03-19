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
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File
import java.lang.reflect.Type


interface QrApiService {
    @Headers("Content-Type: text/plain")
    @POST("/api/check_address")
    suspend fun checkAddress(@Body address: RequestBody): Response<String>
}

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
            QrCameraPreview(navController)
        }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnsafeExperimentalUsageError")
@Composable
fun QrCameraPreview(navController: NavHostController) {
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

class ToStringConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        // Check if the expected response type is a String, return a converter if so
        if (String::class.java == type) {
            return Converter<ResponseBody, String> { responseBody -> responseBody.string() }
        }
        // Return null to continue searching for other converters if the type is not String
        return null
    }
}

@Composable
private fun CameraContent(navController: NavHostController) {

    var lastApiCallTime = 0L  // Initialize to 0

    // Set up Retrofit
    val gson = GsonBuilder().create()
    val retrofit = Retrofit.Builder()
        .baseUrl(MainActivity.ACTIVITY_MANAGEMENT_BASE_URL) // Replace <your_server_ip> with your server's IP address
        .addConverterFactory(ToStringConverterFactory())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val qrApiService = retrofit.create(QrApiService::class.java)

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
                                val requestBody = code.toRequestBody("text/plain".toMediaType())
                                Log.d("QRCodeAnalyzer", "Detected QR Code: $code")

                                val currentTime = System.currentTimeMillis()
                                if (currentTime - lastApiCallTime >= 10000) {  // 5000 milliseconds = 5 seconds
                                    // Update the last API call time
                                    lastApiCallTime = currentTime

                                    // Proceed with your API call here
                                    CoroutineScope(Dispatchers.IO).launch {
                                        val response = qrApiService.checkAddress(requestBody)
                                        Log.d("response", "rrr: $response")
                                        if (response.isSuccessful && response.body() != null) {
                                            val address = response.body()!!
                                            withContext(Dispatchers.Main) {
                                                // Handle your successful response, e.g., navigate to a new page
                                                Log.d("API Success", "Address correct")
                                                navController.navigate("page5")
                                            }
                                        } else {
                                            Log.d("API Error", "Address not found or error in API")
                                        }
                                    }
                                } else {
                                    // If less than 5 seconds have passed since the last API call, you might want to ignore this scan or handle it differently
                                    Log.d("QRCodeAnalyzer", "API call skipped to maintain the 5-second interval")
                                }
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
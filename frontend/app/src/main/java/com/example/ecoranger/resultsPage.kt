package com.example.ecoranger

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ResultsPage(
    navController: NavHostController, context: Context,
    cameraAddress: MutableState<String>
) {
    val createActivityApiService = CreateActivityApiService.create(MainActivity.ACTIVITY_MANAGEMENT_BASE_URL)
    val imageApiService = ImageApiService.create(MainActivity.IMAGE_PROCESSING_BASE_URL)

    val resultText = remember { mutableStateOf("") }
    val isLoading = remember { mutableStateOf(true) }

    // Coroutine scope for launching the network request
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        coroutineScope.launch {
            // Use the saved image file
            val photoFile = File(getOutputDirectory(context), "temp.jpg")
            Log.d("photoFile", "Image is at: ${photoFile.absolutePath}")
            val requestFile = photoFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", photoFile.name, requestFile)

            try {
                val result = imageApiService.inferImage(body)
                resultText.value = result
                // Make additional API request if the result is not "invalid"
                if (result != "invalid") {
                    Log.d("Result", "not invalid")
                    // Example of data for the new API request
                    val data = mapOf(
                        "user_id" to getUserIdFromStorage(context),
                        "activity_type" to cameraAddress.value,
                        "points" to 50
                    )
                    // Launch coroutine for the additional API request
                    try {
                        val response = createActivityApiService.createActivity(data)
                        Log.d("ActivityResponse", "Response: $response")
                    } catch (e: Exception) {
                        Log.e("ActivityError", "Error creating activity: ${e.message}")
                    }
                } else {
                    Log.d("Result", "invalid")
                }
            } catch (e: Exception) {
                resultText.value = "Error: ${e.message}"
            } finally {
                isLoading.value = false // Set loading state to false after the API call completes
            }
        }
    }
    if (isLoading.value) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator() // Show a circular progress indicator while loading
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF254d32)),
                    title = {
                        Text(
                            text = "Back to Home",
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 20.sp, fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate("page0") }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }
                )
            },
            content = {paddingValues: PaddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(top = 20.dp, start = 20.dp, end = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Recyclables detected:",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color.Black
                        )
                    )
                    Text(
                        text = resultText.value,
                        style = TextStyle(
                            fontSize = 24.sp,
                            color = Color.Black
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (resultText.value != "invalid") {
                            "You have earned 50 points"
                        } else {
                            "Please try again"
                        },
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color.Black
                        )
                    )
                }
            }
        )
    }
}



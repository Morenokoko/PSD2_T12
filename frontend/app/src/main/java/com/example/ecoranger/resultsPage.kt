package com.example.ecoranger

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ecoranger.data.Bin
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File


interface ImageApiService {
    @Multipart
    @POST("/infer")
    suspend fun inferImage(@Part image: MultipartBody.Part): String
}

interface ActivityApiService {
    @POST("/api/activities")
    @JvmSuppressWildcards
    suspend fun createActivity(@Body data: Map<String, Any>): ResponseBody
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ResultsPage(navController: NavHostController, context: Context) { // Pass the Context as a parameter
    // Set up Retrofit
    val gson = GsonBuilder().create()
    val retrofit = Retrofit.Builder()
        .baseUrl(MainActivity.IMAGE_PROCESSING_BASE_URL) // Replace <your_server_ip> with your server's IP address
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val imageApiService = retrofit.create(ImageApiService::class.java)

    val retrofit2 = Retrofit.Builder()
        .baseUrl(MainActivity.ACTIVITY_MANAGEMENT_BASE_URL) // Replace <your_server_ip> with your server's IP address
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val activityApiService = retrofit2.create(ActivityApiService::class.java)

    // State to hold the result
    val resultText = remember { mutableStateOf("Results") }

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
                        "activity_type" to "test_activity",
                        "points" to 10
                    )
                    // Launch coroutine for the additional API request
                    try {
                        val response = activityApiService.createActivity(data)
                        Log.d("ActivityResponse", "Response: $response")
                    } catch (e: Exception) {
                        Log.e("ActivityError", "Error creating activity: ${e.message}")
                    }
                }else{
                    Log.d("Result", "invalid")}
            } catch (e: Exception) {
                resultText.value = "Error: ${e.message}"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Back to Home") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("page0") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(padding)) {
                Text(
                    text = resultText.value,
                    modifier = Modifier.padding(16.dp),
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



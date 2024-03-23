package com.example.ecoranger

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.ecoranger.data.CommunityPost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ViewCommPage(navController: NavHostController, postId: String?) {
    val coroutineScope = rememberCoroutineScope()
    val apiService = GetPostByIdApiService.create(MainActivity.CONTENT_MANAGEMENT_BASE_URL)
    var data by remember { mutableStateOf(CommunityPost(null, "", "", "", "", "", 0)) }
    var isLoading by remember { mutableStateOf(true) }
    // Trigger the posts fetch when the screen is composed
    LaunchedEffect(Unit) {
        if (postId != null) {
            Log.d("postid", postId)
        }
        coroutineScope.launch {
            try {
                val response = postId?.let { apiService.getCommPost(it) } // pass id here
                withContext(Dispatchers.Main) {
                    if (response != null) {
                        data = response
                    }
                    isLoading = false
                    Log.d("API_SUCCESS", "Posts list retrieved successfully: $response")
                }
            } catch (e: Exception) {
                // Handle errors
                Log.e("API_ERROR", "Error fetching posts list: ${e.message}", e)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
//                actions = {
//                    Button(
//                        onClick = { },
//                        modifier = Modifier.padding(end = 12.dp)
//                    ) {
//                        Text("Edit")
//                    }
//                }
            )
        },
        content = { paddingValues: PaddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp, 4.dp, 16.dp, 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Title: ${data.title}" // Display title
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Description: ${data.description}" // Display description
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Username: ${data.username}" // Display username
                )
                // You can add more Text composable elements to display other data fields
            }
        }
    )
}

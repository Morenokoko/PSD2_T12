package com.example.ecoranger

import BackHandler
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ecoranger.data.CommunityPost
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CommunityPage(navController: NavHostController, selectedItem: MutableState<Int>) {
    val exitDialogShown = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val apiService = GetCommPostApiService.create(MainActivity.CONTENT_MANAGEMENT_BASE_URL)
    var communityPostList by remember { mutableStateOf<List<CommunityPost>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Trigger the posts fetch when the screen is composed
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val response = apiService.getCommPosts()
                withContext(Dispatchers.Main) {
                    communityPostList = response
                    isLoading = false
                    Log.d("API_SUCCESS", "Posts list retrieved successfully: $response")
                }
            } catch (e: Exception) {
                // Handle errors
                Log.e("API_ERROR", "Error fetching posts list: ${e.message}", e)
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("createPostPage") },
                content = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create post"
                    )
                },
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        content = { paddingValues: PaddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(top = 20.dp, start = 20.dp, end = 20.dp),
            ) {
                Text(
                    text = "Community Posts", style = TextStyle(
                        fontSize = 24.sp, fontWeight = FontWeight.Bold
                    )
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(48.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                } else {
                    if (communityPostList.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "No posts for now...",
                                style = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            // Sample list of community items

                            items(communityPostList) { item ->
                                CommunityCard(item, navController)
                            }
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityCard(item: CommunityPost, navController: NavHostController) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        onClick = {
            val userString = item._id
            navController.navigate("viewPostPage/$userString")
            Log.d("CommunityCard", "Clicked: $userString")
//            navController.navigate("viewPostPage")
        },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFf8f8f8),
        ),
    ) {
        Column(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
        ) {
            Text(
                text = item.title,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = item.description,
                style = TextStyle(
                    fontSize = 14.sp
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Posted by ${item.username} • ${item.dateTime}",
                    style = TextStyle(fontSize = 12.sp),
                    modifier = Modifier.weight(1f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.numComments.toString(),
                        style = TextStyle(fontSize = 12.sp),
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Icon(
                        imageVector = Icons.Outlined.ModeComment,
                        contentDescription = "Number of Comments",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

private fun exitApp() {
    // Close the app
    android.os.Process.killProcess(android.os.Process.myPid())
}

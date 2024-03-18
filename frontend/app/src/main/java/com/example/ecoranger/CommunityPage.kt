package com.example.ecoranger

import BackHandler
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.ecoranger.data.communityPostList
import org.json.JSONObject
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL


// Define a data class for the card information

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CommunityPage(navController: NavHostController, selectedItem: MutableState<Int>) {
    val context = LocalContext.current
    val exitDialogShown = remember { mutableStateOf(false) }
    var communityPosts by remember { mutableStateOf<List<JSONObject>>(emptyList()) }
    var fetchPostsTriggered by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // TODO: Uncomment the following code to fetch the community posts when the API is ready
//    LaunchedEffect(fetchPostsTriggered) {
//        if (fetchPostsTriggered) {
//            try {
//                withContext(Dispatchers.IO) {
//                    val url = URL("${MainActivity.COMMUNITY_BASE_URL}/api/community/posts")
//                    val connection = url.openConnection() as HttpURLConnection
//                    connection.connectTimeout = 30000 // 30 seconds
//                    connection.readTimeout = 30000 // 30 seconds
//                    connection.requestMethod = "GET"
//
//                    val responseCode = connection.responseCode
//                    println("Response Code: $responseCode")
//                    withContext(Dispatchers.Main) {
//                        if (responseCode == HttpURLConnection.HTTP_OK) {
//                            val inputStream = connection.inputStream
//                            val responseBody = inputStream.bufferedReader().use { it.readText() }
//                            println("Response Body: $responseBody")
//                            // Parse the response and update the communityPosts state
//                            val jsonArray = JSONArray(responseBody)
//                            communityPosts = List(jsonArray.length()) { index ->
//                                jsonArray.getJSONObject(index)
//                            }
//                        } else {
//                            // Handle error response
//    val errorStream = connection.errorStream
//    val errorResponseBody = errorStream?.bufferedReader()?.use { it.readText() }
//    val errorResponse = JSONObject(errorResponseBody ?: "{}")
//    errorMessage = errorResponse.getString("error")
//                        }
//                    }
//
//                    connection.disconnect()
//                }
//            } catch (e: Exception) {
//                errorMessage = "An error occurred. Please try again."
//                e.printStackTrace()
//            }
//        }
//    }
//
//    // Trigger the posts fetch when the screen is composed
//    LaunchedEffect(Unit) {
//        fetchPostsTriggered = true
//    }


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
        content = { paddingValues: PaddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(top = 20.dp, start = 20.dp, end = 20.dp),
//                contentPadding = PaddingValues(top = 20.dp, start = 20.dp, end = 20.dp)
            ) {
                Text(
                    text = "Community Posts", style = TextStyle(
                        fontSize = 24.sp, fontWeight = FontWeight.Bold
                    )
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
//                    .padding(paddingValues),
//                contentPadding = PaddingValues(top = 20.dp, start = 20.dp, end = 20.dp)
                ) {
                    // Sample list of community items

                    items(communityPostList) { item ->
                        CommunityCard(item)
                    }
                    // TODO: Uncomment the following code to display the actual community posts when the API is ready
//                    items(communityPosts) { post ->
//                        CommunityCard(
//                            title = post.getString("title"),
//                            description = post.getString("description"),
//                            username = post.getString("username"),
//                            dateTime = post.getString("dateTime"),
//                            numComments = post.getInt("numComments")
//                        )
//                    }
//                    if (errorMessage.isNotEmpty()) {
//                        item {
//                            Text(
//                                text = errorMessage,
//                                color = MaterialTheme.colorScheme.error,
//                                modifier = Modifier.padding(16.dp)
//                            )
//                        }
//                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityCard(item: CommunityPost) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        onClick = {
            // Handle card click if needed
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

//@Composable
//fun formatDateTime(dateTime: Long): String {
//    // Assuming dateTime is in milliseconds
//    val formattedDate = DateFormat.format("dd MMM yyyy", dateTime)
//    val formattedTime = DateFormat.format("h:mm a", dateTime)
//    return "$formattedDate $formattedTime"
//}

private fun exitApp() {
    // Close the app
    android.os.Process.killProcess(android.os.Process.myPid())
}

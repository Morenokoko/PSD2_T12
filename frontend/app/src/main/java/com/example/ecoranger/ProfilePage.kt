package com.example.ecoranger

import BackHandler
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfilePage(
    navController: NavHostController,
    selectedItem: MutableState<Int>,
    logoutAction: () -> Unit
) {
    val context = LocalContext.current
    val exitDialogShown = remember { mutableStateOf(false) }
    var userProfile by remember { mutableStateOf<JSONObject?>(null) }
    var fetchProfileTriggered by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }


    LaunchedEffect(fetchProfileTriggered) {
        if (fetchProfileTriggered) {
            try {
                withContext(Dispatchers.IO) {
                    val url = URL("${MainActivity.USER_MANAGEMENT_BASE_URL}/api/users/profile")
                    val connection = url.openConnection() as HttpURLConnection
                    connection.connectTimeout = 30000 // 30 seconds
                    connection.readTimeout = 30000 // 30 seconds
                    connection.requestMethod = "GET"
                    connection.setRequestProperty("Authorization", getUserIdFromStorage(context))

                    val responseCode = connection.responseCode
                    println("Response Code: $responseCode")
                    withContext(Dispatchers.Main) {
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            val inputStream = connection.inputStream
                            val responseBody = inputStream.bufferedReader().use { it.readText() }
                            println("Response Body: $responseBody")
                            // Parse the response and update the userProfile state
                            userProfile = JSONObject(responseBody)
                        } else {
                            // Handle error response
                            val errorStream = connection.errorStream
                            val errorResponseBody =
                                errorStream?.bufferedReader()?.use { it.readText() }
                            val errorResponse = JSONObject(errorResponseBody ?: "{}")
                            errorMessage = errorResponse.getString("error")
                        }
                    }

                    connection.disconnect()
                }
            } catch (e: Exception) {
                errorMessage = "An error occurred. Please try again."
                e.printStackTrace()
            }
        }
    }

    // Trigger the profile fetch when the screen is composed
    LaunchedEffect(Unit) {
        fetchProfileTriggered = true
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
        content = { paddingValues: PaddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
//                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.clothes),
                    contentDescription = "avatar",
                    contentScale = ContentScale.Crop,            // crop the image if it's not a square
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape)
                )
                Divider(modifier = Modifier.padding(vertical=8.dp))
                if (userProfile != null) {
                    val username = userProfile!!.optString("username", "")
                    val email = userProfile!!.optString("email", "")

                    if (username.isNotEmpty() && email.isNotEmpty()) {
                        ProfileSection(username, email)
//                        Text("Username: $username")
//                        Spacer(modifier = Modifier.padding(8.dp))
//                        Text("Email: $email")
//                        Spacer(modifier = Modifier.padding(16.dp))
                    } else {
                        Text("Error: Missing user information")
                    }
                } else {
                    Text("Loading...")
                }
                Divider(modifier = Modifier.padding(vertical=8.dp))
//                Spacer(modifier = Modifier.weight(1f))
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                HelpAndSupportSection()
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                LogoutButton(onClick = logoutAction)
            }
        }
    )
}


@Composable
fun ProfileSection(username: String, email: String) {
    var isExpanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Profile")
        IconButton(
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                contentDescription = "Toggle Help and Support"
            )
        }
    }

    if (isExpanded) {
        Text("Username: $username")
        Spacer(modifier = Modifier.padding(8.dp))
        Text("Email: $email")
        Spacer(modifier = Modifier.padding(16.dp))
    }
}


@Composable
fun HelpAndSupportSection() {
    var isExpanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Help and Support")
        IconButton(
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                contentDescription = "Toggle Help and Support"
            )
        }
    }

    if (isExpanded) {
        Text(
            "For assistance, please contact our support team at support@ecoranger.com"
        )
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Text("Logout")
    }
}

private fun exitApp() {
    // Close the app
    android.os.Process.killProcess(android.os.Process.myPid())
}

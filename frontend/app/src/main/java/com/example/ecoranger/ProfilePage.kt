package com.example.ecoranger

import BackHandler
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import androidx.compose.ui.platform.LocalContext


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfilePage(navController: NavHostController, selectedItem: MutableState<Int>, logoutAction: () -> Unit) {
    val context = LocalContext.current
    val exitDialogShown = remember { mutableStateOf(false) }
    var userProfile by remember { mutableStateOf<JSONObject?>(null) }
    var fetchProfileTriggered by remember { mutableStateOf(false) }

    LaunchedEffect(fetchProfileTriggered) {
        if (fetchProfileTriggered) {
            try {
                val url = URL("${MainActivity.USER_MANAGEMENT_BASE_URL}/api/users/profile")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Authorization", getUserIdFromStorage(context))

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val responseBody = inputStream.bufferedReader().use { it.readText() }
                    // Parse the response and update the userProfile state
                    userProfile = JSONObject(responseBody)
                }

                connection.disconnect()
            } catch (e: Exception) {
                // TODO: Handle network or other exceptions
                // Handle network or other exceptions
                // Display an error message to the user
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
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                userProfile?.let {
                    Text("Username: ${it.getString("username")}")
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text("Email: ${it.getString("email")}")
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text("Password: ******")
                    Spacer(modifier = Modifier.padding(16.dp))
                    HelpAndSupportButton()
                    Spacer(modifier = Modifier.padding(8.dp))
                    LogoutButton(onClick = logoutAction)
                }
            }
        }
    )
}

@Composable
fun HelpAndSupportButton() {
    Button(
        onClick = { /* TODO Handle help and support button click */ },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Help and Support")
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Logout")
    }
}

private fun exitApp() {
    // Close the app
    android.os.Process.killProcess(android.os.Process.myPid())
}

package com.example.ecoranger

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

fun setLoggedIn(context: Context, isLoggedIn: Boolean) {
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = sharedPreferences.edit()
    editor.putBoolean("isLoggedIn", isLoggedIn)
    editor.apply()
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginPage(navController: NavHostController, context: Context) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginTriggered by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Login") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                LaunchedEffect(loginTriggered) {
                    if (loginTriggered) {
                        try {
                            val loginData = JSONObject().apply {
                                put("username", username)
                                put("password", password)
                            }

                            val url = URL("${MainActivity.USER_MANAGEMENT_BASE_URL}/api/users/login")
                            val connection = url.openConnection() as HttpURLConnection
                            connection.requestMethod = "POST"
                            connection.setRequestProperty("Content-Type", "application/json")
                            connection.doOutput = true

                            val outputStream = connection.outputStream
                            outputStream.write(loginData.toString().toByteArray())
                            outputStream.flush()
                            outputStream.close()

                            val responseCode = connection.responseCode
                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                val inputStream = connection.inputStream
                                val responseBody = inputStream.bufferedReader().use { it.readText() }
                                // Parse the response and extract the user_id
                                val jsonResponse = JSONObject(responseBody)
                                val userId = jsonResponse.getString("user_id")
                                // Store the user_id and login status in shared preferences
                                setLoggedIn(context, userId, true)
                                navController.navigate("page0") {
                                    popUpTo("mainPage") { inclusive = true }
                                }
                            } else {
                                // TODO: Handle login error
                                // Handle login error
                                // Display an error message to the user
                            }

                            connection.disconnect()
                        } catch (e: Exception) {
                            // Handle network or other exceptions
                            // Display an error message to the user
                        }
                    }
                }

                Button(
                    onClick = {
                        loginTriggered = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login")
                }
            }
        }
    )
}

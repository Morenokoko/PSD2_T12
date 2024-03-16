package com.example.ecoranger

import android.annotation.SuppressLint
import android.content.Context
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


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SignUpPage(navController: NavHostController, context: Context) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var signUpTriggered by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sign Up") },
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
                LaunchedEffect(signUpTriggered) {
                    if (signUpTriggered) {
                        try {
                            val signUpData = JSONObject().apply {
                                put("username", username)
                                put("password", password)
                            }

                            val url = URL("${MainActivity.USER_MANAGEMENT_BASE_URL}/api/users/register")
                            val connection = url.openConnection() as HttpURLConnection
                            connection.requestMethod = "POST"
                            connection.setRequestProperty("Content-Type", "application/json")
                            connection.doOutput = true

                            val outputStream = connection.outputStream
                            outputStream.write(signUpData.toString().toByteArray())
                            outputStream.flush()
                            outputStream.close()

                            val responseCode = connection.responseCode
                            if (responseCode == HttpURLConnection.HTTP_CREATED) {
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
                                // TODO: Handle sign-up error
                                // Handle sign-up error
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
                        signUpTriggered = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign Up")
                }
            }
        }
    )
}

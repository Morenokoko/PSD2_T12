package com.example.ecoranger

import BackHandler
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ecoranger.ui.theme.OnlyNotesTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {
    // Public static variables
    companion object {
        const val USER_MANAGEMENT_BASE_URL = "http://localhost:5000"
        const val COMMUNITY_BASE_URL = "http://localhost:5001"
        // Add more base URLs for other microservices
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OnlyNotesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp(this)
                }
            }
        }
    }
}

@Composable
fun MyApp(activity: MainActivity) {
    val navController = rememberNavController()
    val selectedItem = remember { mutableIntStateOf(0) }
    val context = activity.applicationContext
    val sharedPreferences: SharedPreferences =
        activity.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
    val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
    fun logoutUser() {
        with(sharedPreferences.edit()) {
            putBoolean("isLoggedIn", false)
            apply()
        }
        navController.navigate("mainPage")
    }
    NavHost(
        navController, startDestination = if (isLoggedIn) "page0" else "mainPage"
    ) {
        composable("mainPage") { MainPage(navController) }
        composable("loginPage") { LoginPage(navController, context) }
        composable("signUpPage") { SignUpPage(navController, context) }
        composable("page0") { HomePage(navController, selectedItem) }
        composable("page1") { BinsPage(navController, selectedItem) }
        composable("page2") { CameraPage(navController, selectedItem) }
        composable("page3") { CommunityPage(navController, selectedItem) }
        composable("page4") { ProfilePage(navController, selectedItem) { logoutUser() } }
        composable("recyclablesPage") { RecyclablesPage(navController) }
    }
}

@Composable
fun MainPage(navController: NavHostController) {
    val exitDialogShown = remember { mutableStateOf(false) }

    BackHandler(onBackPressed = {
        exitDialogShown.value = true
    })

    if (exitDialogShown.value) {
        ExitConfirmationDialog(onConfirm = {
            exitApp()
        }, onDismiss = {
            exitDialogShown.value = false
        })
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Eco Ranger",
            modifier = Modifier.padding(top = 16.dp),
            style = TextStyle(
                fontSize = 28.sp
            )
        )
        Button(
            onClick = { navController.navigate("loginPage") },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Login")
        }
        Button(
            onClick = { navController.navigate("signUpPage") },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Sign Up")
        }
    }
}

private fun exitApp() {
    // Close the app
    android.os.Process.killProcess(android.os.Process.myPid())
}

fun getUserIdFromStorage(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
    return sharedPreferences.getString("userId", "") ?: ""
}

fun setLoggedIn(context: Context, userId: String, isLoggedIn: Boolean) {
    val sharedPreferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putString("userId", userId)
        putBoolean("isLoggedIn", isLoggedIn)
        apply()
    }
}
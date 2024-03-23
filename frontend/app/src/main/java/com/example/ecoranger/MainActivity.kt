package com.example.ecoranger

import BackHandler
import android.content.Context
import android.content.SharedPreferences
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

class MainActivity : ComponentActivity() {
    // Public static variables
    companion object {
        const val USER_MANAGEMENT_BASE_URL = "http://10.0.2.2:5000"
        const val CONTENT_MANAGEMENT_BASE_URL = "http://10.0.2.2:5001"
        const val RECYCLING_CENTER_BASE_URL = "http://10.0.2.2:5002"
        const val IMAGE_PROCESSING_BASE_URL = "http://10.0.2.2:5003"
        const val ACTIVITY_MANAGEMENT_BASE_URL = "http://10.0.2.2:5004"
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
    val cameraAddress = remember { mutableStateOf("") }
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
        composable("page0") { HomePage(navController, context, selectedItem) }
        composable("page1") { BinsPage(navController, selectedItem) }
        composable("page2") { ScanQrPage(navController, selectedItem, cameraAddress) }
        composable("page3") { CommunityPage(navController, selectedItem) }
        composable("page4") { ProfilePage(navController, selectedItem) { logoutUser() } }
        composable("page5") { ObjectDetectionPage(navController, selectedItem, cameraAddress) }
        composable("recyclablesPage") { RecyclablesPage(navController) }
        composable("resultsPage") { ResultsPage(navController, context, cameraAddress) }
        composable("createPostPage") { CreateCommPage(navController) }
        composable("viewPostPage/{id}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("id")
            ViewCommPage(navController, postId)
//            ViewCommPage(navController)
        }
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
package com.example.ecoranger

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
    val sharedPreferences: SharedPreferences = activity.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
    val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

    if (isLoggedIn) {
        NavHost(navController, startDestination = "page0") {
            composable("mainPage") { MainPage(navController) }
            composable("loginPage") { LoginPage(navController, context) }
            composable("signUpPage") { SignUpPage(navController) }
            composable("page0") { HomePage(navController, selectedItem) }
            composable("page1") { GroupsPage(navController, selectedItem) }
            composable("page2") { NotesPage(navController, selectedItem) }
            composable("page3") { ActivityPage(navController, selectedItem) }
            composable("page4") { SettingsPage(navController, selectedItem) }
        }
    } else {
        NavHost(navController, startDestination = "mainPage") {
            composable("mainPage") { MainPage(navController) }
            composable("loginPage") { LoginPage(navController, context) }
            composable("signUpPage") { SignUpPage(navController) }
            composable("page0") { HomePage(navController, selectedItem) }
            composable("page1") { GroupsPage(navController, selectedItem) }
            composable("page2") { NotesPage(navController, selectedItem) }
            composable("page3") { ActivityPage(navController, selectedItem) }
            composable("page4") { SettingsPage(navController, selectedItem) }
        }
    }
}

@Composable
fun MainPage(navController: NavHostController) {
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
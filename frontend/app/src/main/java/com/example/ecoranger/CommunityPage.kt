package com.example.ecoranger

import BackHandler
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController


// Define a data class for the card information
data class CommunityItem(val title: String, val description: String)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CommunityPage(navController: NavHostController, selectedItem: MutableState<Int>) {
    val exitDialogShown = remember { mutableStateOf(false) }

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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Sample list of community items
                val communityItems = listOf(
                    CommunityItem("Community Item 1", "Description 1"),
                    CommunityItem("Community Item 2", "Description 2"),
                    CommunityItem("Community Item 3", "Description 3"),
                    CommunityItem("Community Item 3", "Description 3"),
                    CommunityItem("Community Item 3", "Description 3"),
                    CommunityItem("Community Item 3", "Description 3"),
                    CommunityItem("Community Item 3", "Description 3"),
                    CommunityItem("Community Item 3", "Description 3"),
                    CommunityItem("Community Item 3", "Description 3"),
                    CommunityItem("Community Item 3", "Description 3")
                    // Add more items as needed
                )

                items(communityItems) { item ->
                    CommunityCard(item)
                }
            }
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(bottom = 56.dp), // Adjust the padding to fit your needs
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text("Welcome to the Activity Page!")
//            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityCard(item: CommunityItem) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        onClick = {
            // Handle card click if needed
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = item.title)
            Text(text = item.description)
        }
    }
}

private fun exitApp() {
    // Close the app
    android.os.Process.killProcess(android.os.Process.myPid())
}

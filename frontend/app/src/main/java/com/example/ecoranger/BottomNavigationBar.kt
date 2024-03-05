package com.example.ecoranger

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController


@Composable
fun BottomNavigationBar(navController: NavHostController, selectedItem: MutableState<Int> = mutableIntStateOf(0)) {

    var selectedItem by remember { selectedItem }
    val items = listOf("Home", "Groups", "Notes", "Activity", "Settings")
    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    when (index) {
                        0 -> Icon(Icons.Filled.Home, contentDescription = "Home")
                        1 -> Icon(Icons.Filled.Favorite, contentDescription = "Groups")
                        2 -> Icon(Icons.Filled.List, contentDescription = "Notes")
                        3 -> Icon(Icons.Filled.Notifications, contentDescription = "Activity")
                        4 -> Icon(Icons.Filled.Settings, contentDescription = "Settings")
                        else -> Icon(Icons.Filled.Home, contentDescription = "Home")
                    }
                },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = {
                    println("Onclick index: $index")
                    selectedItem = index
                    navController.navigate("page$index")
                }
            )
        }
    }
}

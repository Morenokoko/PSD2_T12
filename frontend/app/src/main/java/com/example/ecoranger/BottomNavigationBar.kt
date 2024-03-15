package com.example.ecoranger

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController


@Composable
fun BottomNavigationBar(navController: NavHostController, selectedItem: MutableState<Int> = mutableIntStateOf(0)) {

    var selectedItem by remember { selectedItem }
    val items = listOf("Home", "Bins", "Recycle", "Community", "Profile")
    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    when (index) {
                        0 -> Icon(Icons.Filled.Home, contentDescription = "Home")
                        1 -> Icon(Icons.Filled.LocationOn, contentDescription = "Bins")
                        2 -> Icon(Icons.Filled.Add, contentDescription = "Recycle")
                        3 -> Icon(Icons.Filled.Favorite, contentDescription = "Community")
                        4 -> Icon(Icons.Filled.Person, contentDescription = "Profile")
                        else -> Icon(Icons.Filled.Home, contentDescription = "Home")
                    }
                },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = {
                    println("Onclick index: $index")
                    selectedItem = index
                    navController.navigate("page$index")
                    {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = false
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
//                        restoreState = true
                    }
                }
            )
        }
    }
}

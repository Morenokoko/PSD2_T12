package com.example.ecoranger

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults.containerColor
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController


@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    selectedItem: MutableState<Int> = mutableIntStateOf(0)
) {

    var selectedItem by remember { selectedItem }
    val items = listOf("Home", "Bins", "Recycle", "Groups", "Settings")
    NavigationBar(
        containerColor = Color(0xFF254d32)
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = when (index) {
                            0 -> Icons.Filled.Home
                            1 -> Icons.Filled.LocationOn
                            2 -> Icons.Filled.Recycling
                            3 -> Icons.Filled.Groups
                            4 -> Icons.Filled.Settings
                            else -> Icons.Filled.Home
                        },
                        contentDescription = item,
                    )
                },
                colors = androidx.compose.material3.NavigationBarItemDefaults
                    .colors(
                        selectedTextColor = Color(0xFFd0db97),
                        unselectedTextColor = Color.White,
                        unselectedIconColor = Color.White,
                        selectedIconColor = Color(0xFFd0db97),
                        indicatorColor = Color(0xFF254d32)
                    ),
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

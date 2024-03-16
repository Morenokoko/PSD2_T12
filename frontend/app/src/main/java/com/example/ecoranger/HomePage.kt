package com.example.ecoranger

import BackHandler
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ecoranger.data.activityList

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomePage(navController: NavHostController, selectedItem: MutableState<Int>) {
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


    Scaffold(
        bottomBar = { BottomNavigationBar(navController, selectedItem) },
        content = { paddingValues: PaddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(top = 20.dp, start = 20.dp, end = 20.dp),
                verticalArrangement = Arrangement.Top,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text("Welcome back")
                        Text(
                            text = "John Cena", style = TextStyle(
                                fontSize = 20.sp, fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    ElevatedCard(
                        onClick = {},
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFD0DB97),
                        ),
                    ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "9000", textAlign = TextAlign.Center, style = TextStyle(
                                fontSize = 18.sp, fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "EcoPoints", textAlign = TextAlign.Center
                        )
                    }
                }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp) // Set the desired height here
                        .clickable { navController.navigate("recyclablesPage") }
                        .shadow(                    // Add shadow here
                            elevation = 4.dp,       // Set the elevation (depth) of the shadow
                            shape = RoundedCornerShape(12.dp), // Shape of the shadow
                            clip = true             // Whether to clip the shadow to the shape
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.hands_holding_recyclable_items),
                        contentDescription = null, // Optional content description
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Know Your\nRecyclables", style = TextStyle(
                                fontSize = 32.sp, fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Arrow Right",
                            modifier = Modifier.size(48.dp), // Set the desired size here
//                            tint = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Activity", style = TextStyle(
                        fontSize = 24.sp, fontWeight = FontWeight.Bold
                    )
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                if (activityList != null && activityList.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "No recycling activity\nStart recycling now!",
                            style = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center)
                        )
                    }
                } else {
                    LazyColumn {
                        items(activityList) { activity ->
                            Card(
                                onClick = {},
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFf8f8f8),
                                ),
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                    ) {
                                        Text(
                                            text = "${activity.date} ${activity.time}",
                                        )
                                        Text(
                                            text = "${activity.location}", style = TextStyle(
                                                fontSize = 20.sp
                                            )
                                        )
                                    }
                                    Spacer(Modifier.weight(1f))
                                    Column(
                                        horizontalAlignment = Alignment.End,
                                        verticalArrangement = Arrangement.Center,
                                    ) {
                                        Text(
                                            text = "${activity.points}", style = TextStyle(
                                                fontSize = 20.sp
                                            )
                                        )
                                        Text(
                                            text = "points"
                                        )
                                    }
                                }
                            }
//                            Divider()
                        }
                    }
                }
            }
        })
}


private fun exitApp() {
    // Close the app
    android.os.Process.killProcess(android.os.Process.myPid())
}

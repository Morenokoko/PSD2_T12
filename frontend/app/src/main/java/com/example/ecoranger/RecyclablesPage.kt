package com.example.ecoranger

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

data class RecyclableItem(
    val title: String,
    val description: String,
    @DrawableRes val imageResourceId: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecyclablesPage(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Know Your Recyclables") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                val recyclables = listOf(
                    RecyclableItem(
                        "Paper",
                        "Paper can usually be recycled. Make sure it's clean and dry before recycling.",
                        R.drawable.paper
                    ),
                    RecyclableItem(
                        "Plastic",
                        "Plastic can vary in its recyclability. Check the recycling symbols to know if it can be recycled in your area.",
                        R.drawable.plastic
                    ),
                    RecyclableItem(
                        "Metal",
                        "Metal can be recycled infinitely without losing its quality.",
                        R.drawable.metal
                    ),
                    RecyclableItem(
                        "Glass",
                        "Glass is 100% recyclable and can be recycled endlessly without loss in quality or purity.",
                        R.drawable.glass
                    ),
                    RecyclableItem(
                        "Clothes",
                        "Textiles like clothes can often be recycled or repurposed. Consider donating them to charity or using textile recycling programs to give them a new life.",
                        R.drawable.clothes
                    )
                )
                items(recyclables) { item ->
                    RecyclableItem(item)
                }

            }
        }
    )
}

@Composable
fun RecyclableItem(item: RecyclableItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = item.title,
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(shape = RoundedCornerShape(8.dp))
//                .background(color = Color.LightGray)
        ) {
            Image(
                painter = painterResource(id = item.imageResourceId),//id = R.drawable.hands_holding_recyclable_items
                contentDescription = null, // Optional content description
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Text(
            text = item.description,
            style = TextStyle(
                fontSize = 16.sp
            ),
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

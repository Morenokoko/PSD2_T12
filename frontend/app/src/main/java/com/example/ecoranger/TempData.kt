package com.example.ecoranger

data class Activity(
    val date: String,
    val time: String,
    val location: String,
    val points: Int
)

//val activityList: List<Activity> = emptyList() // for testing

val activityList = listOf(
    Activity("13 Mar 2024", "10:00 AM", "Recycling Bin - Sengkang", 100),
    Activity("12 Mar 2024", "11:30 AM", "Recycling Bin - Waterway Point", 150),
    Activity("11 Mar 2024", "09:45 AM", "Recycling Bin - Fernvale", 200),
    Activity("10 Mar 2024", "02:15 PM", "Recycling Bin - Sengkang", 50),
    Activity("10 Mar 2024", "02:15 PM", "Recycling Bin - Sengkang", 50),
    Activity("10 Mar 2024", "02:15 PM", "Recycling Bin - Sengkang", 50),
    Activity("10 Mar 2024", "02:15 PM", "Recycling Bin - Sengkang", 50),
    Activity("10 Mar 2024", "02:15 PM", "Recycling Bin - Sengkang", 50),
//  Add more activities as needed
)

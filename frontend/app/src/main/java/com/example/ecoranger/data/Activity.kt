package com.example.ecoranger.data

data class Activity(
    val date: String,
    val time: String,
    val points: Int
)

val activityList = listOf(
    Activity("2024-03-01", "10:00 AM", 100),
    Activity("2024-03-02", "11:30 AM", 150),
    Activity("2024-03-03", "09:45 AM", 200),
    Activity("2024-03-04", "02:15 PM", 50),
    // Add more activities as needed
)

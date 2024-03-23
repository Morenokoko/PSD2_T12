package com.example.ecoranger.data

data class CommunityPost(
    val _id: String?,
    val title: String,
    val description: String,
    val username: String,
    val userId: String,
    val dateTime: String,
    val numComments: Int
)

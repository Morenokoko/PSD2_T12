package com.example.ecoranger.data


data class CommunityPost(
    val title: String,
    val description: String,
    val username: String,
    val dateTime: String, // You may want to change the type to Date or Long to represent date and time accurately
    val numComments: Int
)

val communityPostList = listOf(
    CommunityPost("Community Post 1", "Description 1", "UserA", "13 Mar 2024 10:00 AM", 5),
    CommunityPost("Community Post 2", "Description 2", "UserB", "12 Mar 2024 11:30 AM", 8),
    CommunityPost("Community Post 3", "Description 3", "UserC", "11 Mar 2024 09:45 AM", 10),
    CommunityPost("Community Post 4", "Description 4", "UserD", "10 Mar 2024 02:15 PM", 3),
    CommunityPost("Community Post 5", "Description 5", "UserE", "10 Mar 2024 02:15 PM", 6),
    CommunityPost("Community Post 6", "Description 6", "UserF", "10 Mar 2024 02:15 PM", 12),
    CommunityPost("Community Post 7", "Description 7", "UserG", "10 Mar 2024 02:15 PM", 2),
    CommunityPost("Community Post 8", "Description 8", "UserH", "10 Mar 2024 02:15 PM", 4),
    CommunityPost("Community Post 9", "Description 9", "UserI", "10 Mar 2024 02:15 PM", 7),
    CommunityPost("Community Post 10", "Description 10", "UserJ", "10 Mar 2024 02:15 PM", 1),
    CommunityPost("Community Post 11", "Description 11", "UserK", "10 Mar 2024 02:15 PM", 9),
    CommunityPost("Community Post 12", "Description 12", "UserL", "10 Mar 2024 02:15 PM", 15),
    CommunityPost("Community Post 13", "Description 13", "UserM", "10 Mar 2024 02:15 PM", 6),
    CommunityPost("Community Post 14", "Description 14", "UserN", "10 Mar 2024 02:15 PM", 3),
    CommunityPost("Community Post 15", "Description 15", "UserO", "10 Mar 2024 02:15 PM", 8),
    CommunityPost("Community Post 16", "Description 16", "UserP", "10 Mar 2024 02:15 PM", 11),
    CommunityPost("Community Post 17", "Description 17", "UserQ", "10 Mar 2024 02:15 PM", 7),
    CommunityPost("Community Post 18", "Description 18", "UserR", "10 Mar 2024 02:15 PM", 4),
    CommunityPost("Community Post 19", "Description 19", "UserS", "10 Mar 2024 02:15 PM", 9),
    CommunityPost("Community Post 20", "Description 20", "UserT", "10 Mar 2024 02:15 PM", 13)
    // Add more community posts as needed
)
package com.keagan.complete.data.remote

// --- Streak ---
data class StreakDto(
    val current: Int = 0,
    val best: Int = 0,
    val lastCheck: String? = null
)

data class TickRequest(
    val userId: String,
    val date: String // ISO-8601 "2025-10-06"
)

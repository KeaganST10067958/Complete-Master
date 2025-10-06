package com.keagan.complete.data.remote

// --- Motivation quote ---
data class QuoteDto(
    val id: String? = null,
    val text: String = "",
    val author: String? = null,
    val date: String? = null
)

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

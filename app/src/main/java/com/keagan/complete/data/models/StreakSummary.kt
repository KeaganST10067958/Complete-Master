package com.keagan.complete.data.models

data class StreakSummary(
    val current: Int,
    val weekCheckins: List<Boolean> // e.g., [Mon..Sun]
)

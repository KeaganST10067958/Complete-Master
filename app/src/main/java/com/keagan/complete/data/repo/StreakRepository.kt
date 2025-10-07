package com.keagan.complete.data.repo

import com.keagan.complete.data.models.StreakSummary

interface StreakRepository {
    suspend fun fetch(): StreakSummary
    suspend fun checkIn(): StreakSummary

    object Noop : StreakRepository {
        override suspend fun fetch() = StreakSummary(0, List(7) { false })
        override suspend fun checkIn() = StreakSummary(1, listOf(true) + List(6) { false })
    }
}

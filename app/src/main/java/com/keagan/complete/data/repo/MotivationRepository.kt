package com.keagan.complete.data.repo

import com.keagan.complete.data.models.Quote

interface MotivationRepository {
    suspend fun fetchToday(): Quote

    object Noop : MotivationRepository {
        override suspend fun fetchToday() =
            Quote("Keep going. Future you is watching.", "PlanDemic")
    }
}

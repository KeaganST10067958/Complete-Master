package com.keagan.complete.data.placeholder

import android.content.Context
import java.time.LocalDate

object QuoteProvider {
    private val quotes = listOf(
        "Small progress is still progress." to "PlanDemic",
        "Plans make goals possible." to "PlanDemic",
        "Show up today. Your future self will thank you." to "PlanDemic",
        "Consistency beats intensity." to "PlanDemic",
        "One page, one plan, one step." to "PlanDemic"
    )

    fun getTodaysQuote(@Suppress("UNUSED_PARAMETER") context: Context): Pair<String, String> {
        val index = LocalDate.now().dayOfYear % quotes.size
        return quotes[index]
    }
}

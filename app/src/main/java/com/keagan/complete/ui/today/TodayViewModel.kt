package com.keagan.complete.ui.today

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.keagan.complete.data.models.Quote
import com.keagan.complete.data.models.StreakSummary
import com.keagan.complete.data.repo.MotivationRepository
import com.keagan.complete.data.repo.StreakRepository

class TodayViewModel(
    // DI later; for now, simple defaults
    private val motivationRepo: MotivationRepository = MotivationRepository.Noop,
    private val streakRepo: StreakRepository = StreakRepository.Noop
) : ViewModel() {

    private val _quote = MutableLiveData<Quote?>()
    val quote: LiveData<Quote?> = _quote

    private val _streak = MutableLiveData<StreakSummary?>()
    val streak: LiveData<StreakSummary?> = _streak

    fun bootstrap() {
        // Placeholder baseline UI
        _quote.value = Quote("Stay focused. One step at a time.", "PlanDemic")
        _streak.value = StreakSummary(current = 0, weekCheckins = List(7) { false })
    }

    fun refreshQuote() {
        // TODO: motivationRepo.fetchToday()
        // _quote.postValue(...)
    }

    fun checkInToday() {
        // TODO: streakRepo.checkIn()
        // _streak.postValue(updated)
    }
}

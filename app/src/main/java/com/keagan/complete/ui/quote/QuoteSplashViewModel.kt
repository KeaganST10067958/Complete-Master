package com.keagan.complete.ui.quote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keagan.complete.data.remote.MotivationApi
import com.keagan.complete.data.remote.QuoteDto
import kotlinx.coroutines.launch

class QuoteSplashViewModel : ViewModel() {
    private val _quote = MutableLiveData<QuoteDto>()
    val quote: LiveData<QuoteDto> = _quote

    fun load() {
        viewModelScope.launch {
            try { _quote.postValue(MotivationApi.service.random()) }
            catch (_: Exception) { _quote.postValue(QuoteDto("Keep going — future you will thank you.")) }
        }
    }
}

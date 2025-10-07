package com.keagan.complete.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class QuoteDto(val quote: String, val author: String? = "Unknown")

interface MotivationService {
    @GET("quotes/random")
    suspend fun random(): QuoteDto
}

object MotivationApi {
    // LIVE base URL for your Render service (keep the trailing slash)
    private const val BASE_URL = "https://quotes-api-render.onrender.com/"

    val service: MotivationService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MotivationService::class.java)
    }
}

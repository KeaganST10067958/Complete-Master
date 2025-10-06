package com.keagan.complete.data.remote

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * PLACEHOLDER. Wire this later in a Repo/ViewModel.
 */
interface StreakService {
    // e.g. GET https://api.yourdomain.com/streak?userId=123
    @GET("streak")
    fun getStreak(@Query("userId") userId: String): Call<StreakDto>

    // e.g. POST https://api.yourdomain.com/streak/tick
    @POST("streak/tick")
    fun tick(@Body body: TickRequest): Call<StreakDto>
}

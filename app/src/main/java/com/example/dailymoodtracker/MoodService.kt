package com.example.dailymoodtracker

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MoodService {

    @GET("moods")
    fun getAllMoods(): Call<List<MoodData>>

    @POST("add_mood")
    fun addMood(@Body mood: MoodData): Call<Void>
}
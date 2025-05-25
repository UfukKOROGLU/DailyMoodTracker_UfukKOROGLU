package com.example.dailymoodtracker

data class MoodData(
    val id: Int? = null,
    val mood: String,
    val note: String,
    val timestamp: String? = null
)
package com.example.dailymoodtracker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log

class MainActivity : AppCompatActivity() {

    private lateinit var moodSpinner: Spinner
    private lateinit var noteInput: EditText
    private lateinit var saveButton: Button
    private lateinit var historyButton: Button

    private val emojiMoodMap = mapOf(
        "Happy" to "😊 Happy",
        "Sad" to "😢 Sad",
        "Angry" to "😡 Angry",
        "Tired" to "😴 Tired",
        "Neutral" to "😐 Neutral"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }


        moodSpinner = findViewById(R.id.moodSpinner)
        noteInput = findViewById(R.id.noteInput)
        saveButton = findViewById(R.id.saveButton)
        historyButton = findViewById(R.id.historyButton)


        val emojiMoodList = emojiMoodMap.values.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, emojiMoodList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        moodSpinner.adapter = adapter


        saveButton.setOnClickListener {
            val selectedEmojiMood = moodSpinner.selectedItem.toString()
            val selectedMood = emojiMoodMap.entries.find { it.value == selectedEmojiMood }?.key ?: "Neutral"
            val note = noteInput.text.toString()

            if (note.isEmpty()) {
                Toast.makeText(this, "Please enter a note", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val moodData = MoodData(mood = selectedMood, note = note)

            ApiClient.moodService.addMood(moodData).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@MainActivity, "Mood saved!", Toast.LENGTH_SHORT).show()
                        noteInput.text.clear()
                    } else {
                        Toast.makeText(this@MainActivity, "Failed to save mood", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }


        historyButton.setOnClickListener {
            val intent = Intent(this, MoodHistoryActivity::class.java)
            startActivity(intent)
        }


        val chartButton = findViewById<Button>(R.id.chartButton)
        chartButton.setOnClickListener {
            startActivity(Intent(this, MoodChartActivity::class.java))
        }


        scheduleDailyReminder()
    }

    private fun scheduleDailyReminder() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(this, "Please allow exact alarms in system settings", Toast.LENGTH_LONG).show()

                val intent = Intent().apply {
                    action = android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                }
                startActivity(intent)
                return
            }
        }

        val intent = Intent(this, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        Log.d("ReminderSetup", "Alarm set for: ${calendar.time}")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }
}
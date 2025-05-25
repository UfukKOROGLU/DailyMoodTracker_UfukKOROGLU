package com.example.dailymoodtracker

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class MoodChartActivity : AppCompatActivity() {

    private lateinit var moodBarChart: BarChart

    private fun getEmojiMoodLabel(mood: String): String {
        return when (mood) {
            "Happy" -> "😊 Happy"
            "Sad" -> "😢 Sad"
            "Angry" -> "😡 Angry"
            "Tired" -> "😴 Tired"
            "Neutral" -> "😐 Neutral"
            else -> mood
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        moodBarChart = findViewById(R.id.moodBarChart)

        ApiClient.moodService.getAllMoods().enqueue(object : Callback<List<MoodData>> {
            override fun onResponse(call: Call<List<MoodData>>, response: Response<List<MoodData>>) {
                if (response.isSuccessful && response.body() != null) {
                    val moodList = response.body()!!
                    showChart(processData(moodList))
                }

                val moodList = response.body()!!
                showChart(processData(moodList))

                val mostFrequent = findMostFrequentMood(moodList)
                val mostTextView = findViewById<TextView>(R.id.textViewMostFrequent)
                mostTextView.text = "Most frequent mood this week: ${getEmojiMoodLabel(mostFrequent)}"
            }

            override fun onFailure(call: Call<List<MoodData>>, t: Throwable) {

            }
        })
    }

    private fun processData(moods: List<MoodData>): Map<String, Int> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = Calendar.getInstance()
        val dataMap = mutableMapOf<String, Int>()

        // Son 7 gün
        for (i in 6 downTo 0) {
            val cal = today.clone() as Calendar
            cal.add(Calendar.DAY_OF_YEAR, -i)
            val dateKey = dateFormat.format(cal.time)
            dataMap[dateKey] = 0
        }

        for (mood in moods) {
            val date = mood.timestamp?.substring(0, 10) ?: continue
            if (date in dataMap) {
                dataMap[date] = dataMap[date]!! + 1
            }
        }

        return dataMap
    }

    private fun showChart(dataMap: Map<String, Int>) {
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()
        var index = 0f

        dataMap.forEach { (date, count) ->
            entries.add(BarEntry(index, count.toFloat()))
            labels.add(date.substring(5)) // ay-gün
            index++
        }

        val dataSet = BarDataSet(entries, "Mood Entries")
        dataSet.color = Color.rgb(100, 149, 237)
        val barData = BarData(dataSet)
        barData.barWidth = 0.9f

        moodBarChart.data = barData
        moodBarChart.setFitBars(true)
        moodBarChart.description.isEnabled = false
        moodBarChart.axisRight.isEnabled = false
        moodBarChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        moodBarChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        moodBarChart.xAxis.granularity = 1f
        moodBarChart.animateY(1000)
        moodBarChart.invalidate()
    }

    private fun findMostFrequentMood(moods: List<MoodData>): String {
        val today = Calendar.getInstance()
        val sevenDaysAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -6) }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val filtered = moods.filter {
            val date = it.timestamp?.substring(0, 10)
            val cal = Calendar.getInstance()
            if (date != null) {
                cal.time = dateFormat.parse(date)
                !cal.before(sevenDaysAgo) && !cal.after(today)
            } else false
        }

        val most = filtered.groupingBy { it.mood }.eachCount().maxByOrNull { it.value }?.key ?: "N/A"
        return most
    }


}
package com.example.dailymoodtracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MoodHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MoodAdapter
    private var moodList: List<MoodData> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        recyclerView = findViewById(R.id.recyclerViewMoods)
        recyclerView.layoutManager = LinearLayoutManager(this)


        ApiClient.moodService.getAllMoods().enqueue(object : Callback<List<MoodData>> {
            override fun onResponse(call: Call<List<MoodData>>, response: Response<List<MoodData>>) {
                if (response.isSuccessful && response.body() != null) {
                    moodList = response.body()!!
                    adapter = MoodAdapter(moodList)
                    recyclerView.adapter = adapter
                } else {
                    Toast.makeText(this@MoodHistoryActivity, "No mood data found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<MoodData>>, t: Throwable) {
                Toast.makeText(this@MoodHistoryActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })


        val exportButton = findViewById<Button>(R.id.buttonExport)
        exportButton.setOnClickListener {
            exportMoodData()
        }
    }

    private fun exportMoodData() {
        if (moodList.isEmpty()) {
            Toast.makeText(this, "No mood data to export", Toast.LENGTH_SHORT).show()
            return
        }


        val content = StringBuilder()
        for (mood in moodList) {
            content.append("Mood: ${mood.mood}\n")
            content.append("Note: ${mood.note}\n")
            content.append("Date: ${mood.timestamp}\n\n")
        }

        try {

            val fileName = "mood_history.txt"
            val file = File(getExternalFilesDir(null), fileName)
            file.writeText(content.toString())


            val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)


            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "My Mood History")
                putExtra(Intent.EXTRA_TEXT, "See attached file.")
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(intent, "Share mood history via"))

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
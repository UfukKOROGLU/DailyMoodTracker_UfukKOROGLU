package com.example.dailymoodtracker

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MoodAdapter(private val moodList: List<MoodData>) : RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {

    class MoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val moodText: TextView = itemView.findViewById(R.id.textViewMood)
        val noteText: TextView = itemView.findViewById(R.id.textViewNote)
        val timeText: TextView = itemView.findViewById(R.id.textViewTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mood, parent, false)
        return MoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val mood = moodList[position]
        holder.moodText.text = getEmojiMoodLabel(mood.mood)
        holder.noteText.text = mood.note
        holder.timeText.text = mood.timestamp


        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            AlertDialog.Builder(context)
                .setTitle(getEmojiMoodLabel(mood.mood))
                .setMessage("📝 Note: ${mood.note}\n\n🕒 Time: ${mood.timestamp}")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    override fun getItemCount(): Int = moodList.size

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
}
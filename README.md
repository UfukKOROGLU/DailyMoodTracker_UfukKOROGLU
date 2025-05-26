# Daily Mood Tracker – SE 373 Make-Up Project

This is a full-stack mobile application developed as part of the SE 373 Make-Up Project by Ufuk Bora Köroğlu (230709004).

## 📱 Android Features

- Daily mood selection via emoji-labeled spinner
- Note entry for each mood
- History screen with RecyclerView and CardView
- Weekly mood summary using MPAndroidChart
- Mood export to text file and share via email
- **Daily notification at 9:00 AM** reminding user to log their mood

## 🔔 Alarm Behavior

- The alarm uses Android's `AlarmManager` to trigger a reminder **at 09:00 AM UTC time.**
- Due to UTC (GMT+0) usage, if you are testing in Turkey (GMT+3), set the alarm for **06:00 AM UTC** to match **09:00 AM local time.**
- The notification will work even when the app is closed (uses a `BroadcastReceiver`).
- Make sure exact alarms are allowed in your device settings (especially Android 12+).

## 🌐 Backend Integration (Flask + MySQL)

The backend (Flask + MySQL) is not included in this GitHub repository.  
Instead, it is shared in a separate ZIP file containing:

- Flask API (`app.py`)
- MySQL database dump file (`mood_tracker.sql`)

⚠️ To make the app work with your own backend:

- The `BASE_URL` in `ApiClient.kt` (line 7) must be updated with your **local IP address**.  
  Example:

```kotlin
const val BASE_URL = "http://192.168.X.X:5000/"

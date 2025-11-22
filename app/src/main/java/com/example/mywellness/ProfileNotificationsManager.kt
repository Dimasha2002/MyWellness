package com.example.mywellness

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.cardview.widget.CardView

class ProfileNotificationsManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)

    fun setupNotificationSettings(notificationsCard: CardView) {
        notificationsCard.setOnClickListener {
            // Navigate to notification settings page
            val intent = Intent(context, NotificationSettingsActivity::class.java)
            context.startActivity(intent)
        }
    }

    fun enableHabitReminders(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("habit_reminders", enabled).apply()
    }

    fun enableMoodReminders(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("mood_reminders", enabled).apply()
    }

    fun enableProgressUpdates(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("progress_updates", enabled).apply()
    }

    fun setReminderTime(hour: Int, minute: Int) {
        sharedPreferences.edit()
            .putInt("reminder_hour", hour)
            .putInt("reminder_minute", minute)
            .apply()
    }

    fun isHabitRemindersEnabled(): Boolean = 
        sharedPreferences.getBoolean("habit_reminders", true)

    fun isMoodRemindersEnabled(): Boolean = 
        sharedPreferences.getBoolean("mood_reminders", true)

    fun isProgressUpdatesEnabled(): Boolean = 
        sharedPreferences.getBoolean("progress_updates", true)

    fun getReminderHour(): Int = sharedPreferences.getInt("reminder_hour", 20)
    
    fun getReminderMinute(): Int = sharedPreferences.getInt("reminder_minute", 0)
}
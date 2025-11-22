package com.example.mywellness

import android.content.Context
import android.content.SharedPreferences
import android.widget.TextView

class ProfileHeaderManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)

    fun updateProfileStats(streakDaysText: TextView, completionText: TextView, achievementsText: TextView) {
        // Load current stats from SharedPreferences
        val streakDays = sharedPreferences.getInt("streak_days", 0)
        val completionRate = sharedPreferences.getInt("completion_rate", 0)
        val achievements = sharedPreferences.getInt("achievements_count", 0)

        // Update UI
        streakDaysText.text = streakDays.toString()
        completionText.text = "$completionRate%"
        achievementsText.text = achievements.toString()
    }

    fun updateStreak(newStreak: Int) {
        sharedPreferences.edit().putInt("streak_days", newStreak).apply()
    }

    fun updateCompletionRate(newRate: Int) {
        sharedPreferences.edit().putInt("completion_rate", newRate).apply()
    }

    fun updateAchievements(newCount: Int) {
        sharedPreferences.edit().putInt("achievements_count", newCount).apply()
    }

    fun getStreakDays(): Int = sharedPreferences.getInt("streak_days", 0)
    
    fun getCompletionRate(): Int = sharedPreferences.getInt("completion_rate", 0)
    
    fun getAchievementsCount(): Int = sharedPreferences.getInt("achievements_count", 0)
}
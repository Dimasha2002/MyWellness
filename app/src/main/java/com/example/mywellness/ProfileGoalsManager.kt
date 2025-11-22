package com.example.mywellness

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.cardview.widget.CardView

class ProfileGoalsManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("goals_prefs", Context.MODE_PRIVATE)

    fun setupGoalSettings(goalsCard: CardView) {
        goalsCard.setOnClickListener {
            // Navigate to goals settings page
            val intent = Intent(context, GoalsSettingsActivity::class.java)
            context.startActivity(intent)
        }
    }

    fun setDailyGoal(goalCount: Int) {
        sharedPreferences.edit().putInt("daily_goal", goalCount).apply()
    }

    fun setWeeklyGoal(goalCount: Int) {
        sharedPreferences.edit().putInt("weekly_goal", goalCount).apply()
    }

    fun setMonthlyGoal(goalCount: Int) {
        sharedPreferences.edit().putInt("monthly_goal", goalCount).apply()
    }

    fun updateDailyProgress(completed: Int) {
        sharedPreferences.edit().putInt("daily_progress", completed).apply()
    }

    fun updateWeeklyProgress(completed: Int) {
        sharedPreferences.edit().putInt("weekly_progress", completed).apply()
    }

    fun updateMonthlyProgress(completed: Int) {
        sharedPreferences.edit().putInt("monthly_progress", completed).apply()
    }

    fun getDailyGoal(): Int = sharedPreferences.getInt("daily_goal", 3)
    
    fun getWeeklyGoal(): Int = sharedPreferences.getInt("weekly_goal", 21)
    
    fun getMonthlyGoal(): Int = sharedPreferences.getInt("monthly_goal", 90)

    fun getDailyProgress(): Int = sharedPreferences.getInt("daily_progress", 0)
    
    fun getWeeklyProgress(): Int = sharedPreferences.getInt("weekly_progress", 0)
    
    fun getMonthlyProgress(): Int = sharedPreferences.getInt("monthly_progress", 0)

    fun calculateDailyProgressPercentage(): Int {
        val goal = getDailyGoal()
        val progress = getDailyProgress()
        return if (goal > 0) ((progress.toFloat() / goal) * 100).toInt() else 0
    }

    fun calculateWeeklyProgressPercentage(): Int {
        val goal = getWeeklyGoal()
        val progress = getWeeklyProgress()
        return if (goal > 0) ((progress.toFloat() / goal) * 100).toInt() else 0
    }

    fun calculateMonthlyProgressPercentage(): Int {
        val goal = getMonthlyGoal()
        val progress = getMonthlyProgress()
        return if (goal > 0) ((progress.toFloat() / goal) * 100).toInt() else 0
    }
}
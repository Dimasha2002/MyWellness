package com.example.mywellness

import android.content.Context
import android.content.SharedPreferences

class HabitProgressManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("MyWellnessHabits", Context.MODE_PRIVATE)
    
    private val progressPreferences: SharedPreferences = 
        context.getSharedPreferences("HabitProgress", Context.MODE_PRIVATE)
    
    fun updateHabitProgress() {
        val completedCount = getCompletedHabits()
        val totalCount = getTotalHabits()
        val percentage = if (totalCount > 0) (completedCount * 100) / totalCount else 0
        
        progressPreferences.edit()
            .putInt("completed_habits", completedCount)
            .putInt("total_habits", totalCount)
            .putInt("habit_percentage", percentage)
            .apply()
    }
    
    fun getCompletedHabits(): Int {
        var completed = 0
        
        // Check default habits
        val defaultHabits = mapOf(
            "drink_water" to "Drink Water",
            "eat_meals" to "Eat 3 Main Meals",
            "exercise" to "Exercise", 
            "read_book" to "Read Book",
            "meditate" to "Meditate",
            "sleep" to "Sleep Well"
        )
        
        for (habitKey in defaultHabits.keys) {
            if (sharedPreferences.getBoolean(habitKey, false)) {
                completed++
            }
        }
        
        // Check custom habits
        val customHabitsCount = sharedPreferences.getInt("custom_habits_count", 0)
        for (i in 0 until customHabitsCount) {
            if (sharedPreferences.getBoolean("custom_habit_$i", false)) {
                completed++
            }
        }
        
        return completed
    }
    
    fun getTotalHabits(): Int {
        val defaultHabitsCount = 6 // drink_water, eat_meals, exercise, read_book, meditate, sleep
        val customHabitsCount = sharedPreferences.getInt("custom_habits_count", 0)
        return defaultHabitsCount + customHabitsCount
    }
    
    fun getHabitProgressPercentage(): Int {
        return progressPreferences.getInt("habit_percentage", 0)
    }
    
    fun saveMealTime(time: String) {
        val currentTimes = progressPreferences.getStringSet("meal_times", mutableSetOf()) ?: mutableSetOf()
        currentTimes.add(time)
        progressPreferences.edit()
            .putStringSet("meal_times", currentTimes)
            .apply()
    }
    
    fun saveWaterTime(time: String) {
        val currentTimes = progressPreferences.getStringSet("water_times", mutableSetOf()) ?: mutableSetOf()
        currentTimes.add(time)
        progressPreferences.edit()
            .putStringSet("water_times", currentTimes)
            .apply()
    }
    
    fun getMealTimes(): Set<String> {
        return progressPreferences.getStringSet("meal_times", setOf()) ?: setOf()
    }
    
    fun getWaterTimes(): Set<String> {
        return progressPreferences.getStringSet("water_times", setOf()) ?: setOf()
    }
    
    fun clearDailyData() {
        progressPreferences.edit()
            .remove("meal_times")
            .remove("water_times")
            .apply()
    }
}
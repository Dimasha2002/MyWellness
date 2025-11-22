package com.example.mywellness

import android.content.Context
import android.content.Intent
import android.widget.LinearLayout

class NavigationHelper(private val context: Context) {

    fun setupBottomNavigation() {
        // Home Tab
        val navHome = (context as android.app.Activity).findViewById<LinearLayout>(R.id.nav_home)
        navHome?.setOnClickListener {
            val intent = Intent(context, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            context.startActivity(intent)
        }

        // Habits Tab
        val navHabits = context.findViewById<LinearLayout>(R.id.nav_habits)
        navHabits?.setOnClickListener {
            val intent = Intent(context, HabitActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            context.startActivity(intent)
        }

        // Mood Tab
        val navMood = context.findViewById<LinearLayout>(R.id.nav_mood)
        navMood?.setOnClickListener {
            val intent = Intent(context, MoodActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            context.startActivity(intent)
        }

        // Profile Tab
        val navProfile = context.findViewById<LinearLayout>(R.id.nav_profile)
        navProfile?.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            context.startActivity(intent)
        }
    }
}
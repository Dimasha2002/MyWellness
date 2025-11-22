package com.example.mywellness

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class GoalsSettingsActivity : AppCompatActivity() {

    private lateinit var goalsManager: ProfileGoalsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goals_settings)
        
        // Hide action bar for clean UI
        supportActionBar?.hide()
        
        // Initialize manager
        goalsManager = ProfileGoalsManager(this)
        
        setupHeader()
        setupGoalOptions()
        loadCurrentGoals()
        setupBottomNavigation()
    }

    private fun setupHeader() {
        // Setup back button
        findViewById<ImageView>(R.id.back_button).setOnClickListener {
            finish()
        }
        
        // Set title
        findViewById<TextView>(R.id.page_title).text = "Goals & Targets"
    }

    private fun setupGoalOptions() {
        // CardViews are now display-only, no click listeners needed
        // All goal information is displayed statically
    }

    private fun loadCurrentGoals() {
        // Load and display current goals
        val dailyGoal = goalsManager.getDailyGoal()
        val weeklyGoal = goalsManager.getWeeklyGoal()
        val monthlyGoal = goalsManager.getMonthlyGoal()
        
        val dailyProgress = goalsManager.getDailyProgress()
        val weeklyProgress = goalsManager.getWeeklyProgress()
        val monthlyProgress = goalsManager.getMonthlyProgress()
        
        // Update UI
        findViewById<TextView>(R.id.dailyGoalText).text = "$dailyProgress / $dailyGoal habits"
        findViewById<TextView>(R.id.dailyProgressText).text = "${goalsManager.calculateDailyProgressPercentage()}% complete"
        
        findViewById<TextView>(R.id.weeklyGoalText).text = "$weeklyProgress / $weeklyGoal habits"
        findViewById<TextView>(R.id.weeklyProgressText).text = "${goalsManager.calculateWeeklyProgressPercentage()}% complete"
        
        findViewById<TextView>(R.id.monthlyGoalText).text = "$monthlyProgress / $monthlyGoal habits"
        findViewById<TextView>(R.id.monthlyProgressText).text = "${goalsManager.calculateMonthlyProgressPercentage()}% complete"
    }

    private fun setupBottomNavigation() {
        val navigationHelper = NavigationHelper(this)
        navigationHelper.setupBottomNavigation()
    }
}
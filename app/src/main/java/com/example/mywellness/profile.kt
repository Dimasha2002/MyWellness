package com.example.mywellness

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class ProfileActivity : AppCompatActivity() {
    
    // Manager instances for modular functionality
    private lateinit var profileHeaderManager: ProfileHeaderManager
    private lateinit var profileAccountManager: ProfileAccountManager
    private lateinit var profileNotificationsManager: ProfileNotificationsManager
    private lateinit var profileGoalsManager: ProfileGoalsManager
    private lateinit var profilePrivacyManager: ProfilePrivacyManager
    private lateinit var profileAboutManager: ProfileAboutManager
    
    private lateinit var sharedPreferences: android.content.SharedPreferences
    private var username: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.mywellness.R.layout.profile)
        // Hide action bar for clean UI
        supportActionBar?.hide()
        // Initialize managers
        initializeManagers()
        sharedPreferences = getSharedPreferences("MyWellnessUser", MODE_PRIVATE)
        username = sharedPreferences.getString("username", "") ?: ""
        updateProfileName()
        // Setup profile sections
        // setupProfileHeader() // Removed stats section from UI
        setupProfileOptions()
        setupBottomNavigation()
    }
    override fun onResume() {
        super.onResume()
        username = sharedPreferences.getString("username", "") ?: ""
        updateProfileName()
    }
    private fun updateProfileName() {
        val nameView = findViewById<TextView>(com.example.mywellness.R.id.profile_name)
        if (nameView != null) {
            val nameToShow = if (username.isNotEmpty()) username else "User"
            nameView.text = nameToShow
        }
    }
    
    private fun initializeManagers() {
        profileHeaderManager = ProfileHeaderManager(this)
        profileAccountManager = ProfileAccountManager(this)
        profileNotificationsManager = ProfileNotificationsManager(this)
        profileGoalsManager = ProfileGoalsManager(this)
        profilePrivacyManager = ProfilePrivacyManager(this)
        profileAboutManager = ProfileAboutManager(this)
    }
    
    /*
    private fun setupProfileHeader() {
        // Find TextViews for stats and update them - REMOVED: Stats section no longer exists in UI
        val streakCountText = findViewById<TextView>(com.example.mywellness.R.id.streak_count)
        val completionRateText = findViewById<TextView>(com.example.mywellness.R.id.completion_rate)
        val achievementsCountText = findViewById<TextView>(com.example.mywellness.R.id.achievements_count)
        
        // Update profile stats using the manager
        profileHeaderManager.updateProfileStats(streakCountText, completionRateText, achievementsCountText)
    }
    */
    
    private fun setupProfileOptions() {
        // Setup each section using the respective managers
        
        // Account settings
        val accountCard = findViewById<CardView>(com.example.mywellness.R.id.accountCard)
        profileAccountManager.setupAccountSettings(accountCard)
        
        // Notifications
        val notificationsCard = findViewById<CardView>(com.example.mywellness.R.id.notificationsCard)
        profileNotificationsManager.setupNotificationSettings(notificationsCard)
        
        // Goals
        val goalsCard = findViewById<CardView>(com.example.mywellness.R.id.goalsCard)
        profileGoalsManager.setupGoalSettings(goalsCard)
        
        // Privacy
        val privacyCard = findViewById<CardView>(com.example.mywellness.R.id.privacyCard)
        profilePrivacyManager.setupPrivacySettings(privacyCard)
        
        // About
        val aboutCard = findViewById<CardView>(com.example.mywellness.R.id.aboutCard)
        profileAboutManager.setupAboutSettings(aboutCard)
    }
    
    private fun setupBottomNavigation() {
        // Home navigation
        findViewById<LinearLayout>(com.example.mywellness.R.id.nav_home).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        
        // Habits navigation
        findViewById<LinearLayout>(com.example.mywellness.R.id.nav_habits).setOnClickListener {
            startActivity(Intent(this, HabitActivity::class.java))
            finish()
        }
        
        // Mood navigation
        findViewById<LinearLayout>(com.example.mywellness.R.id.nav_mood).setOnClickListener {
            startActivity(Intent(this, MoodActivity::class.java))
            finish()
        }
        
        // Profile navigation (current page)
        findViewById<LinearLayout>(com.example.mywellness.R.id.nav_profile).setOnClickListener {
            Toast.makeText(this, "You're already on Profile! 👤", Toast.LENGTH_SHORT).show()
        }
    }
}


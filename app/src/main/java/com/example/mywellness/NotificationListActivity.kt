package com.example.mywellness

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.util.*

class NotificationListActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_list)
        
        // Hide action bar for clean UI
        supportActionBar?.hide()
        
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyWellnessUser", MODE_PRIVATE)
        
        setupHeader()
        loadNotifications()
        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        loadNotifications()
    }

    private fun setupHeader() {
        // Setup back button
        findViewById<ImageView>(R.id.back_button).setOnClickListener {
            finish()
        }
        
        // Setup settings button
        findViewById<ImageView>(R.id.settings_button).setOnClickListener {
            startActivity(android.content.Intent(this, NotificationSettingsActivity::class.java))
        }
        
        // Set title
        findViewById<TextView>(R.id.page_title).text = "Notifications"
    }

    private fun loadNotifications() {
        val notificationContainer = findViewById<LinearLayout>(R.id.notificationContainer)
        notificationContainer.removeAllViews()
        
        // Check if notifications are enabled
        val waterEnabled = sharedPreferences.getBoolean("water_reminders_enabled", false)
        val mealEnabled = sharedPreferences.getBoolean("meal_reminders_enabled", false)
        
        var hasNotifications = false
        
        if (waterEnabled) {
            addWaterReminderCard(notificationContainer)
            hasNotifications = true
        }
        
        if (mealEnabled) {
            addMealReminderCard(notificationContainer)
            hasNotifications = true
        }
        
        if (!hasNotifications) {
            addNoNotificationsCard(notificationContainer)
        }
    }

    private fun addWaterReminderCard(container: LinearLayout) {
        val waterIntervalSeconds = sharedPreferences.getInt("water_interval_seconds", 3600) // Default 1 hour
        val lastWaterTime = sharedPreferences.getLong("last_water_reminder", System.currentTimeMillis())
        val nextWaterTime = lastWaterTime + (waterIntervalSeconds * 1000)
        val timeUntilNext = nextWaterTime - System.currentTimeMillis()
        
        val cardView = layoutInflater.inflate(R.layout.notification_item_card, container, false) as CardView
        
        val iconText = cardView.findViewById<TextView>(R.id.notification_icon)
        val titleText = cardView.findViewById<TextView>(R.id.notification_title)
        val messageText = cardView.findViewById<TextView>(R.id.notification_message)
        val timeText = cardView.findViewById<TextView>(R.id.notification_time)
        
        iconText.text = "💧"
        titleText.text = "Hydration Reminder"
        
        if (timeUntilNext > 0) {
            val timeString = formatTimeUntil(timeUntilNext)
            messageText.text = "Stay hydrated throughout the day"
            timeText.text = "Next reminder in $timeString"
            timeText.setTextColor(0xFF4CAF50.toInt()) // Green
        } else {
            messageText.text = "Time to drink some water!"
            timeText.text = "Reminder overdue"
            timeText.setTextColor(0xFFF44336.toInt()) // Red
        }
        
        container.addView(cardView)
    }

    private fun addMealReminderCard(container: LinearLayout) {
        val mealIntervalSeconds = sharedPreferences.getInt("meal_interval_seconds", 14400) // Default 4 hours
        
        val cardView = layoutInflater.inflate(R.layout.notification_item_card, container, false) as CardView
        
        val iconText = cardView.findViewById<TextView>(R.id.notification_icon)
        val titleText = cardView.findViewById<TextView>(R.id.notification_title)
        val messageText = cardView.findViewById<TextView>(R.id.notification_message)
        val timeText = cardView.findViewById<TextView>(R.id.notification_time)
        
        iconText.text = "🍽️"
        titleText.text = "Meal Reminder"
        messageText.text = "Don't forget to nourish your body"
        
        val intervalString = formatMealInterval(mealIntervalSeconds)
        timeText.text = "Every $intervalString"
        timeText.setTextColor(0xFF2196F3.toInt()) // Blue
        
        container.addView(cardView)
    }

    private fun addNoNotificationsCard(container: LinearLayout) {
        val cardView = layoutInflater.inflate(R.layout.notification_item_card, container, false) as CardView
        
        val iconText = cardView.findViewById<TextView>(R.id.notification_icon)
        val titleText = cardView.findViewById<TextView>(R.id.notification_title)
        val messageText = cardView.findViewById<TextView>(R.id.notification_message)
        val timeText = cardView.findViewById<TextView>(R.id.notification_time)
        
        iconText.text = "🔔"
        titleText.text = "No Active Reminders"
        messageText.text = "Set up hydration and meal reminders to stay healthy"
        timeText.text = "Tap settings to configure"
        timeText.setTextColor(0xFF9E9E9E.toInt()) // Gray
        
        cardView.setOnClickListener {
            startActivity(android.content.Intent(this, NotificationSettingsActivity::class.java))
        }
        
        container.addView(cardView)
    }

    private fun formatTimeUntil(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m ${seconds}s"
            else -> "${seconds}s"
        }
    }
    
    private fun formatMealInterval(seconds: Int): String {
        return when {
            seconds >= 3600 -> "${seconds / 3600}h"
            seconds >= 60 -> "${seconds / 60}m"
            else -> "${seconds}s"
        }
    }

    private fun setupBottomNavigation() {
        val navigationHelper = NavigationHelper(this)
        navigationHelper.setupBottomNavigation()
    }
}
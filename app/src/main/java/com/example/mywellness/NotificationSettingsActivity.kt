package com.example.mywellness

import android.os.Bundle
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.AdapterView
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class NotificationSettingsActivity : AppCompatActivity() {

    private lateinit var notificationsManager: ProfileNotificationsManager
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var notificationManagerHelper: NotificationManagerHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_settings)
        
        // Hide action bar for clean UI
        supportActionBar?.hide()
        
        // Initialize managers and preferences
        notificationsManager = ProfileNotificationsManager(this)
        sharedPreferences = getSharedPreferences("MyWellnessUser", MODE_PRIVATE)
        notificationManagerHelper = NotificationManagerHelper(this)
        
        setupHeader()
        requestNotificationPermission()
        setupNotificationOptions()
        setupSpinners()
        loadCurrentSettings()
        setupBottomNavigation()
    }

    private fun setupHeader() {
        // Setup back button
        findViewById<ImageView>(R.id.back_button).setOnClickListener {
            finish()
        }
        
        // Set title
        findViewById<TextView>(R.id.page_title).text = "Notifications"
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }
    }

    private fun setupNotificationOptions() {
        // Habit Reminders Switch
        findViewById<Switch>(R.id.habitRemindersSwitch).setOnCheckedChangeListener { _, isChecked ->
            notificationsManager.enableHabitReminders(isChecked)
        }
        
        // Mood Reminders Switch
        findViewById<Switch>(R.id.moodRemindersSwitch).setOnCheckedChangeListener { _, isChecked ->
            notificationsManager.enableMoodReminders(isChecked)
        }
        
        // Progress Updates Switch
        findViewById<Switch>(R.id.progressUpdatesSwitch).setOnCheckedChangeListener { _, isChecked ->
            notificationsManager.enableProgressUpdates(isChecked)
        }
        
        // Water Reminders Switch
        findViewById<Switch>(R.id.waterRemindersSwitch).setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("water_reminders_enabled", isChecked)
            editor.apply()
            
            findViewById<LinearLayout>(R.id.waterIntervalLayout).visibility = 
                if (isChecked) android.view.View.VISIBLE else android.view.View.GONE
            
            if (isChecked) {
                val intervalSeconds = getSelectedWaterInterval()
                notificationManagerHelper.scheduleHydrationReminders(intervalSeconds)
                // Show immediate test notification
                notificationManagerHelper.showHydrationNotification()
            } else {
                notificationManagerHelper.cancelHydrationReminders()
            }
        }
        
        // Meal Reminders Switch
        findViewById<Switch>(R.id.mealRemindersSwitch).setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("meal_reminders_enabled", isChecked)
            editor.apply()
            
            findViewById<LinearLayout>(R.id.mealIntervalLayout).visibility = 
                if (isChecked) android.view.View.VISIBLE else android.view.View.GONE
            
            if (isChecked) {
                val intervalSeconds = getSelectedMealInterval()
                notificationManagerHelper.scheduleMealReminders(intervalSeconds)
                // Show immediate test notification
                notificationManagerHelper.showMealNotification()
            } else {
                notificationManagerHelper.cancelMealReminders()
            }
        }
    }

    private fun setupSpinners() {
        // Setup Water Interval Spinner
        val waterIntervalSpinner = findViewById<Spinner>(R.id.waterIntervalSpinner)
        val waterIntervals = resources.getStringArray(R.array.water_reminder_intervals)
        val waterAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, waterIntervals)
        waterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        waterIntervalSpinner.adapter = waterAdapter
        
        waterIntervalSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val waterValues = resources.getStringArray(R.array.water_reminder_values)
                val intervalSeconds = waterValues[position].toInt()
                
                val editor = sharedPreferences.edit()
                editor.putInt("water_interval_seconds", intervalSeconds)
                editor.putLong("water_interval_set_time", System.currentTimeMillis())
                editor.putString("water_interval_display", waterIntervals[position])
                editor.apply()
                
                // Reschedule reminders with new interval if enabled
                if (sharedPreferences.getBoolean("water_reminders_enabled", false)) {
                    notificationManagerHelper.scheduleHydrationReminders(intervalSeconds)
                    // Show confirmation notification with exact timing
                    val intervalText = waterIntervals[position]
                    notificationManagerHelper.showTestNotification("✅ Water reminders saved! Next reminder in $intervalText")
                }
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        
        // Setup Meal Interval Spinner
        val mealIntervalSpinner = findViewById<Spinner>(R.id.mealIntervalSpinner)
        val mealIntervals = resources.getStringArray(R.array.meal_reminder_intervals)
        val mealAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mealIntervals)
        mealAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mealIntervalSpinner.adapter = mealAdapter
        
        mealIntervalSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val mealValues = resources.getStringArray(R.array.meal_reminder_values)
                val intervalSeconds = mealValues[position].toInt()
                
                val editor = sharedPreferences.edit()
                editor.putInt("meal_interval_seconds", intervalSeconds)
                editor.putLong("meal_interval_set_time", System.currentTimeMillis())
                editor.putString("meal_interval_display", mealIntervals[position])
                editor.apply()
                
                // Reschedule reminders with new interval if enabled
                if (sharedPreferences.getBoolean("meal_reminders_enabled", false)) {
                    notificationManagerHelper.scheduleMealReminders(intervalSeconds)
                    // Show confirmation notification with exact timing
                    val intervalText = mealIntervals[position]
                    notificationManagerHelper.showTestNotification("✅ Meal reminders saved! Next reminder in $intervalText")
                }
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun getSelectedWaterInterval(): Int {
        val spinner = findViewById<Spinner>(R.id.waterIntervalSpinner)
        val waterValues = resources.getStringArray(R.array.water_reminder_values)
        return waterValues[spinner.selectedItemPosition].toInt()
    }

    private fun getSelectedMealInterval(): Int {
        val spinner = findViewById<Spinner>(R.id.mealIntervalSpinner)
        val mealValues = resources.getStringArray(R.array.meal_reminder_values)
        return mealValues[spinner.selectedItemPosition].toInt()
    }

    private fun loadCurrentSettings() {
        // Load current settings from SharedPreferences
        findViewById<Switch>(R.id.habitRemindersSwitch).isChecked = notificationsManager.isHabitRemindersEnabled()
        findViewById<Switch>(R.id.moodRemindersSwitch).isChecked = notificationsManager.isMoodRemindersEnabled()
        findViewById<Switch>(R.id.progressUpdatesSwitch).isChecked = notificationsManager.isProgressUpdatesEnabled()
        
        // Load water reminder settings
        val waterEnabled = sharedPreferences.getBoolean("water_reminders_enabled", false)
        findViewById<Switch>(R.id.waterRemindersSwitch).isChecked = waterEnabled
        findViewById<LinearLayout>(R.id.waterIntervalLayout).visibility = 
            if (waterEnabled) android.view.View.VISIBLE else android.view.View.GONE
        
        // Set water interval spinner selection
        val savedWaterInterval = sharedPreferences.getInt("water_interval_seconds", 3600) // Default 1 hour
        val waterValues = resources.getStringArray(R.array.water_reminder_values)
        val waterPosition = waterValues.indexOfFirst { it.toInt() == savedWaterInterval }
        if (waterPosition >= 0) {
            val waterSpinner = findViewById<Spinner>(R.id.waterIntervalSpinner)
            waterSpinner.post {
                waterSpinner.setSelection(waterPosition, false) // false = don't trigger listener
            }
        }
        
        // Load meal reminder settings
        val mealEnabled = sharedPreferences.getBoolean("meal_reminders_enabled", false)
        findViewById<Switch>(R.id.mealRemindersSwitch).isChecked = mealEnabled
        findViewById<LinearLayout>(R.id.mealIntervalLayout).visibility = 
            if (mealEnabled) android.view.View.VISIBLE else android.view.View.GONE
        
        // Set meal interval spinner selection
        val savedMealInterval = sharedPreferences.getInt("meal_interval_seconds", 14400) // Default 4 hours
        val mealValues = resources.getStringArray(R.array.meal_reminder_values)
        val mealPosition = mealValues.indexOfFirst { it.toInt() == savedMealInterval }
        if (mealPosition >= 0) {
            val mealSpinner = findViewById<Spinner>(R.id.mealIntervalSpinner)
            mealSpinner.post {
                mealSpinner.setSelection(mealPosition, false) // false = don't trigger listener
            }
        }
    }

    private fun setupBottomNavigation() {
        val navigationHelper = NavigationHelper(this)
        navigationHelper.setupBottomNavigation()
    }
}
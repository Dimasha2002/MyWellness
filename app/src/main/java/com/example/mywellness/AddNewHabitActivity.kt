package com.example.mywellness

import android.app.TimePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class AddNewHabitActivity : AppCompatActivity() {

    private lateinit var etHabitName: EditText
    private lateinit var etHabitDescription: EditText
    private lateinit var btnBack: ImageButton
    private lateinit var btnCancel: TextView
    private lateinit var btnCreateHabit: TextView
    private lateinit var tvSelectedTime: TextView
    private lateinit var timePickerLayout: LinearLayout
    private lateinit var ivTimeIcon: ImageView
    
    // Icon selection
    private lateinit var iconWater: LinearLayout
    private lateinit var iconMeditation: LinearLayout
    private lateinit var iconExercise: LinearLayout
    private lateinit var iconReading: LinearLayout
    private lateinit var iconSleep: LinearLayout
    private lateinit var iconMusic: LinearLayout
    private lateinit var iconHealthyFood: LinearLayout
    private lateinit var iconMindfulness: LinearLayout
    
    // Frequency selection
    private lateinit var freqDaily: TextView
    private lateinit var freqWeekly: TextView
    
    private var selectedIcon: String = "💧" // Default icon
    private var selectedFrequency: String = "Daily" // Default frequency
    private var selectedTime: String = "09:00 AM" // Default time
    private var hasReminderEnabled: Boolean = false // Default: no reminder
    private var isUpdateMode: Boolean = false
    private var habitIdToUpdate: String = ""
    
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_new_habit)

        // Hide action bar
        supportActionBar?.hide()
        
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyWellnessHabits", MODE_PRIVATE)

        initializeViews()
        setupClickListeners()
        setupBackNavigation()
        
        // Initialize reminder display
        updateReminderDisplay()
        
        // Check if this is an update operation
        handleIntentExtras()
    }

    private fun initializeViews() {
        etHabitName = findViewById(R.id.et_habit_name)
        etHabitDescription = findViewById(R.id.et_habit_description)
        btnBack = findViewById(R.id.btn_back)
        btnCancel = findViewById(R.id.btn_cancel)
        btnCreateHabit = findViewById(R.id.btn_create_habit)
        tvSelectedTime = findViewById(R.id.tv_selected_time)
        timePickerLayout = findViewById(R.id.time_picker_layout)
        ivTimeIcon = findViewById(R.id.iv_time_icon)
        
        // Icon views
        iconWater = findViewById(R.id.icon_water)
        iconMeditation = findViewById(R.id.icon_meditation)
        iconExercise = findViewById(R.id.icon_exercise)
        iconReading = findViewById(R.id.icon_reading)
        iconSleep = findViewById(R.id.icon_sleep)
        iconMusic = findViewById(R.id.icon_music)
        iconHealthyFood = findViewById(R.id.icon_healthy_food)
        iconMindfulness = findViewById(R.id.icon_mindfulness)
        
        // Frequency views
        freqDaily = findViewById(R.id.freq_daily)
        freqWeekly = findViewById(R.id.freq_weekly)
        
        // Set default selections
        updateIconSelection(iconWater, "💧")
        updateFrequencySelection(freqDaily, "Daily")
        updateReminderDisplay()
    }

    private fun setupClickListeners() {
        // Back button
        btnBack.setOnClickListener {
            finish()
        }

        // Cancel button
        btnCancel.setOnClickListener {
            finish()
        }

        // Create habit button
        btnCreateHabit.setOnClickListener {
            saveNewHabit()
        }

        // Time picker
        timePickerLayout.setOnClickListener {
            if (hasReminderEnabled) {
                // If reminder is enabled, clicking allows changing the time
                showTimePicker()
            } else {
                // If no reminder, clicking enables reminder and shows time picker
                hasReminderEnabled = true
                showTimePicker()
            }
        }

        // Clear reminder button
        ivTimeIcon.setOnClickListener {
            if (hasReminderEnabled) {
                // Clear the reminder
                hasReminderEnabled = false
                updateReminderDisplay()
            } else {
                // Enable reminder and show time picker
                hasReminderEnabled = true
                showTimePicker()
            }
        }

        // Icon selection listeners
        iconWater.setOnClickListener {
            updateIconSelection(iconWater, "💧")
        }

        iconMeditation.setOnClickListener {
            updateIconSelection(iconMeditation, "🧘")
        }

        iconExercise.setOnClickListener {
            updateIconSelection(iconExercise, "🏃")
        }

        iconReading.setOnClickListener {
            updateIconSelection(iconReading, "📚")
        }

        iconSleep.setOnClickListener {
            updateIconSelection(iconSleep, "😴")
        }

        iconMusic.setOnClickListener {
            updateIconSelection(iconMusic, "🎵")
        }

        iconHealthyFood.setOnClickListener {
            updateIconSelection(iconHealthyFood, "🥗")
        }

        iconMindfulness.setOnClickListener {
            updateIconSelection(iconMindfulness, "🌸")
        }

        // Frequency selection listeners
        freqDaily.setOnClickListener {
            updateFrequencySelection(freqDaily, "Daily")
        }

        freqWeekly.setOnClickListener {
            updateFrequencySelection(freqWeekly, "Weekly")
        }
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                selectedTime = timeFormat.format(calendar.time)
                hasReminderEnabled = true
                updateReminderDisplay()
            },
            hour,
            minute,
            false // Use 12-hour format
        )
        timePickerDialog.show()
    }

    private fun updateIconSelection(selectedView: LinearLayout, icon: String) {
        // Reset all icon backgrounds
        val allIcons = listOf(iconWater, iconMeditation, iconExercise, iconReading, iconSleep, iconMusic, iconHealthyFood, iconMindfulness)
        allIcons.forEach { 
            it.setBackgroundResource(R.drawable.white_rounded_background)
        }
        
        // Highlight selected icon with blue color
        selectedView.setBackgroundResource(R.drawable.blue_button_selected)
        selectedIcon = icon
    }

    private fun updateFrequencySelection(selectedView: TextView, frequency: String) {
        // Reset frequency button colors
        freqDaily.setBackgroundResource(R.drawable.white_rounded_background)
        freqDaily.setTextColor(resources.getColor(R.color.gray_600))
        
        freqWeekly.setBackgroundResource(R.drawable.white_rounded_background)
        freqWeekly.setTextColor(resources.getColor(R.color.gray_600))
        
        // Highlight selected frequency with blue color
        selectedView.setBackgroundResource(R.drawable.blue_button_selected)
        selectedView.setTextColor(resources.getColor(android.R.color.white))
        selectedFrequency = frequency
    }

    private fun updateReminderDisplay() {
        if (hasReminderEnabled) {
            tvSelectedTime.text = selectedTime
            tvSelectedTime.setTextColor(resources.getColor(android.R.color.black))
            // Show clear/remove icon
            ivTimeIcon.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            ivTimeIcon.setColorFilter(resources.getColor(android.R.color.holo_red_light))
        } else {
            tvSelectedTime.text = "Tap to set reminder"
            tvSelectedTime.setTextColor(resources.getColor(android.R.color.darker_gray))
            // Show clock icon
            ivTimeIcon.setImageResource(android.R.drawable.ic_menu_recent_history)
            ivTimeIcon.setColorFilter(resources.getColor(android.R.color.darker_gray))
        }
    }

    private fun saveNewHabit() {
        val habitName = etHabitName.text.toString().trim()
        val habitDescription = etHabitDescription.text.toString().trim()

        if (habitName.isEmpty()) {
            showCustomToast("Please enter a habit name")
            return
        }

        if (habitDescription.isEmpty()) {
            showCustomToast("Please enter a habit description")
            return
        }

        // Handle habit creation or update
        val editor = sharedPreferences.edit()
        
        if (isUpdateMode) {
            // Update existing habit
            updateExistingHabit(habitName, habitDescription, editor)
        } else {
            // Create new habit - use existing logic
            val habitId = "custom_habit_${System.currentTimeMillis()}"
            val habitCount = sharedPreferences.getInt("habit_count", 0)
            val newHabitCount = habitCount + 1
            
            editor.putString("habit_${newHabitCount}_id", habitId)
            editor.putString("habit_${newHabitCount}_name", habitName)
            editor.putString("habit_${newHabitCount}_description", habitDescription)
            editor.putString("habit_${newHabitCount}_icon", selectedIcon)
            editor.putString("habit_${newHabitCount}_frequency", selectedFrequency)
            if (hasReminderEnabled) {
                editor.putString("habit_${newHabitCount}_time", selectedTime)
            } else {
                editor.putString("habit_${newHabitCount}_time", "")
            }
            editor.putBoolean("habit_${newHabitCount}_completed", false)
            editor.putInt("habit_count", newHabitCount)
        }
        
        editor.apply()
        
        val resultIntent = Intent().apply {
            putExtra("habit_description", habitDescription)
            putExtra("habit_icon", selectedIcon)
            putExtra("habit_frequency", selectedFrequency)
            putExtra("habit_time", if (hasReminderEnabled) selectedTime else "")
            putExtra("habit_added", true)
            putExtra("is_update", isUpdateMode)
        }
        
        setResult(RESULT_OK, resultIntent)
        
        val message = if (isUpdateMode) {
            "${selectedIcon} Habit updated successfully!"
        } else {
            "${selectedIcon} Habit created successfully!"
        }
        showCustomToast(message)
        
        // Navigate back to habits screen
        finish()
    }
    
    private fun updateExistingHabit(habitName: String, habitDescription: String, editor: SharedPreferences.Editor) {
        // Find and update the existing habit
        val habitCount = sharedPreferences.getInt("habit_count", 0)
        
        for (i in 1..habitCount) {
            val id = sharedPreferences.getString("habit_${i}_id", "")
            if (id == habitIdToUpdate) {
                // Update this habit
                editor.putString("habit_${i}_name", habitName)
                editor.putString("habit_${i}_description", habitDescription)
                editor.putString("habit_${i}_icon", selectedIcon)
                editor.putString("habit_${i}_frequency", selectedFrequency)
                editor.putString("habit_${i}_time", selectedTime)
                // Keep the existing completion state
                break
            }
        }
    }

    private fun showCustomToast(message: String) {
        val inflater = LayoutInflater.from(this)
        val layout = inflater.inflate(R.layout.custom_toast, null)
        
        val toastText = layout.findViewById<TextView>(R.id.toast_text)
        toastText.text = message
        
        val toast = Toast(this)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }

    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }
    
    private fun handleIntentExtras() {
        isUpdateMode = intent.getBooleanExtra("is_update", false)
        if (isUpdateMode) {
            // Update mode - populate fields with existing data
            habitIdToUpdate = intent.getStringExtra("habit_id") ?: ""
            val habitName = intent.getStringExtra("habit_name") ?: ""
            val habitDescription = intent.getStringExtra("habit_description") ?: ""
            val habitIcon = intent.getStringExtra("habit_icon") ?: "💧"
            val habitFrequency = intent.getStringExtra("habit_frequency") ?: "Daily"
            val habitTime = intent.getStringExtra("habit_time") ?: "09:00 AM"
            
            etHabitName.setText(habitName)
            etHabitDescription.setText(habitDescription)
            selectedIcon = habitIcon
            selectedFrequency = habitFrequency
            selectedTime = habitTime
            tvSelectedTime.text = selectedTime
            
            // Update button text
            btnCreateHabit.text = "Update Habit"
            
            // Set the correct icon selection
            updateIconSelectionFromValue(habitIcon)
            
            // Set the correct frequency selection
            updateFrequencySelectionFromValue(habitFrequency)
        }
    }
    
    private fun updateIconSelectionFromValue(icon: String) {
        val iconMap = mapOf(
            "💧" to iconWater,
            "🧘" to iconMeditation,
            "🏃" to iconExercise,
            "📚" to iconReading,
            "😴" to iconSleep,
            "🎵" to iconMusic,
            "🥗" to iconHealthyFood,
            "🌸" to iconMindfulness
        )
        
        iconMap[icon]?.let { selectedIconView ->
            updateIconSelection(selectedIconView, icon)
        }
    }
    
    private fun updateFrequencySelectionFromValue(frequency: String) {
        when (frequency) {
            "Daily" -> updateFrequencySelection(freqDaily, "Daily")
            "Weekly" -> updateFrequencySelection(freqWeekly, "Weekly")
        }
    }
}
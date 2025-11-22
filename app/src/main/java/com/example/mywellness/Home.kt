package com.example.mywellness

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.view.View
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.cardview.widget.CardView
import com.example.mywellness.HabitProgressManager

class HomeActivity : AppCompatActivity() {
    
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var habitProgressManager: HabitProgressManager
    
    private var currentMeals = 0
    private val maxMeals = 3
    private var currentWater = 0
    private val maxWater = 8
    private var habitsProgress = 0
    private var username: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.mywellness.R.layout.home)
        
        // Hide action bar for clean UI
        supportActionBar?.hide()
        
        // Initialize SharedPreferences and HabitProgressManager
        sharedPreferences = getSharedPreferences("MyWellnessUser", MODE_PRIVATE)
        habitProgressManager = HabitProgressManager(this)
        
        setupClickListeners()
        loadSavedData()
        updateUI()
        updateUsernameDisplay()
    }
    
    override fun onResume() {
        super.onResume()
        loadSavedData()
        updateUI()
        updateUsernameDisplay()
    }
    
    private fun setupClickListeners() {
        // Notification icon - navigate to notification list
        findViewById<android.widget.ImageView>(com.example.mywellness.R.id.notificationIcon)?.setOnClickListener {
            startActivity(Intent(this, NotificationListActivity::class.java))
        }
        
        // Meals card - show meal tracker popup
        findViewById<CardView>(com.example.mywellness.R.id.mealsCard).setOnClickListener {
            showMealTrackerPopup()
        }
        
        // Water card - show water tracker popup
        findViewById<CardView>(com.example.mywellness.R.id.waterCard).setOnClickListener {
            showWaterTrackerPopup()
        }
        
        // Habits card - navigate to habit page
        findViewById<CardView>(com.example.mywellness.R.id.habitsCard).setOnClickListener {
            startActivity(Intent(this, HabitActivity::class.java))
        }
        
        // Bottom navigation
        findViewById<android.widget.LinearLayout>(com.example.mywellness.R.id.nav_home).setOnClickListener {
            // Already on home - show message
            Toast.makeText(this, "You're already on Home! 🏠", Toast.LENGTH_SHORT).show()
        }
        
        findViewById<android.widget.LinearLayout>(com.example.mywellness.R.id.nav_habits).setOnClickListener {
            startActivity(Intent(this, HabitActivity::class.java))
            finish()
        }
        
        findViewById<android.widget.LinearLayout>(com.example.mywellness.R.id.nav_mood).setOnClickListener {
            startActivity(Intent(this, MoodActivity::class.java))
            finish()
        }
        
        findViewById<android.widget.LinearLayout>(com.example.mywellness.R.id.nav_profile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }
    
    private fun loadSavedData() {
        // Load meal data
        currentMeals = sharedPreferences.getInt("current_meals", 0)
        // Load water data
        currentWater = sharedPreferences.getInt("current_water", 0)
        // Load habits progress from HabitProgressManager
        habitsProgress = habitProgressManager.getHabitProgressPercentage()
        // Load username
        username = sharedPreferences.getString("username", "") ?: ""
        // Update progress bar immediately (without animation) for instant feedback
        updateProgressBar(habitsProgress)
        findViewById<TextView>(com.example.mywellness.R.id.habitsPercentage).text = "$habitsProgress%"
    }
    private fun updateUsernameDisplay() {
        val welcomeText = findViewById<TextView>(com.example.mywellness.R.id.welcomeText)
        if (welcomeText != null) {
            val nameToShow = if (username.isNotEmpty()) username else "User"
            welcomeText.text = "Good Morning, $nameToShow!"
        }
    }
    
    private fun saveData() {
        val editor = sharedPreferences.edit()
        editor.putInt("current_meals", currentMeals)
        editor.putInt("current_water", currentWater)
        editor.apply()
    }
    

    
    private fun updateUI() {
        updateMealsDisplay()
        updateWaterDisplay()
        animateHabitsProgress()
        updateWeeklyHabitChart()
    }
    
    private fun updateMealsDisplay() {
        findViewById<TextView>(com.example.mywellness.R.id.mealsCount).text = "$currentMeals/$maxMeals"
    }
    
    private fun updateWaterDisplay() {
        findViewById<TextView>(com.example.mywellness.R.id.waterCount).text = "$currentWater/$maxWater"
    }
    
    private fun animateHabitsProgress() {
        val habitsPercentage = findViewById<TextView>(com.example.mywellness.R.id.habitsPercentage)
        val progressView = findViewById<android.view.View>(com.example.mywellness.R.id.habitsProgress)
        
        val animator = ValueAnimator.ofInt(0, habitsProgress)
        animator.duration = 1500
        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Int
            habitsPercentage.text = "$progress%"
            
            // Update progress bar visual fill
            updateProgressBar(progress)
        }
        animator.start()
    }
    
    private fun updateProgressBar(percentage: Int) {
        val progressView = findViewById<android.view.View>(com.example.mywellness.R.id.habitsProgress)
        val progressParent = progressView.parent as android.widget.LinearLayout
        
        // Ensure percentage is within valid range
        val validPercentage = percentage.coerceIn(0, 100)
        
        // Update progress view weight
        val progressParams = progressView.layoutParams as android.widget.LinearLayout.LayoutParams
        progressParams.weight = validPercentage.toFloat()
        progressView.layoutParams = progressParams
        
        // Update remaining view weight (if it exists)
        if (progressParent.childCount > 1) {
            val remainingView = progressParent.getChildAt(1)
            val remainingParams = remainingView.layoutParams as android.widget.LinearLayout.LayoutParams
            remainingParams.weight = (100 - validPercentage).toFloat()
            remainingView.layoutParams = remainingParams
        }
    }
    
    private fun updateWeeklyHabitChart() {
        val weeklyBarChart = findViewById<LinearLayout>(com.example.mywellness.R.id.weeklyBarChart)
        weeklyBarChart.removeAllViews()
        
        // Get current day of week (0 = Sunday, 1 = Monday, ..., 6 = Saturday)
        val calendar = Calendar.getInstance()
        val todayIndex = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> 6    // Sunday is last in our chart
            Calendar.MONDAY -> 0    // Monday is first
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5  // Saturday is today
            else -> 0
        }
        
        // Get weekly data with today's actual progress
        val weeklyPercentages = getWeeklyHabitPercentages(todayIndex)
        var totalPercentage = 0
        
        // Create chart container with background
        weeklyBarChart.setBackgroundColor(0xFFF8F7FF.toInt())
        
        for (i in 0..6) { // 7 days (Mon-Sun)
            val dayPercentage = weeklyPercentages[i]
            totalPercentage += dayPercentage
            
            // Create container for each bar with percentage label
            val barContainer = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                gravity = android.view.Gravity.BOTTOM
                val horizontalMargin = resources.getDimensionPixelSize(android.R.dimen.app_icon_size) / 8
                setPadding(horizontalMargin, 0, horizontalMargin, 0)
            }
            
            // Add percentage label on top of bar
            val percentageLabel = TextView(this).apply {
                text = "${dayPercentage}%"
                textSize = 10f // Slightly smaller for better fit
                setTextColor(0xFF666666.toInt()) // Slightly lighter for better visibility
                gravity = android.view.Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = resources.getDimensionPixelSize(android.R.dimen.app_icon_size) / 16
                }
            }
            
            // Create the bar
            val bar = View(this).apply {
                // Chart container is 180dp high, but we need to reserve space for the label
                val chartContainerHeight = 180 // dp
                val labelHeight = 18 // Reserve 18dp for the percentage label
                val availableBarHeight = chartContainerHeight - labelHeight
                
                // Calculate actual bar height based on percentage relative to available height
                // For real-time data, show actual percentage; for future days, show 0
                val actualPercentage = if (dayPercentage == 0 && i > todayIndex) 0 else dayPercentage
                val barHeight = ((actualPercentage * availableBarHeight / 100).coerceAtLeast(if (actualPercentage > 0) 6 else 0)).coerceAtMost(availableBarHeight)
                val heightInPx = (barHeight * resources.displayMetrics.density).toInt()
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 
                    heightInPx
                ).apply {
                    setMargins(0, 2, 0, 0)
                }
                
                // Highlight today's bar with different color
                background = GradientDrawable().apply {
                    setColor(if (i == todayIndex) 0xFF3B82F6.toInt() else 0xFFB3D9FF.toInt())
                    cornerRadius = 6f
                }
            }
            
            barContainer.addView(percentageLabel)
            barContainer.addView(bar)
            weeklyBarChart.addView(barContainer)
        }
        
        // Update weekly percentage display
        val averagePercentage = if (totalPercentage > 0) totalPercentage / 7 else 0
        findViewById<TextView>(com.example.mywellness.R.id.weeklyPercentage).text = "${averagePercentage}%"
    }
    
    private fun getWeeklyHabitPercentages(todayIndex: Int): IntArray {
        // Initialize with sample data for past days only (up to today)
        val weeklyData = intArrayOf(
            75,  // Monday - past day
            85,  // Tuesday - past day
            70,  // Wednesday - past day
            90,  // Thursday - past day
            80,  // Friday - past day
            0,   // Saturday (today - will be updated with real data)
            0    // Sunday - future day, no data yet
        )
        
        // Update today's data with actual habit progress
        weeklyData[todayIndex] = habitsProgress
        
        // Clear future days (any day after today should be 0)
        for (i in (todayIndex + 1)..6) {
            weeklyData[i] = 0
        }
        
        // Get stored data for previous days (if you want to persist weekly data)
        // For now, keeping sample data for demonstration
        
        return weeklyData
    }
    
    private fun setActiveTab(tabName: String) {
        // Reset all tabs to inactive color
        findViewById<TextView>(com.example.mywellness.R.id.nav_home_text).apply {
            setTextColor(resources.getColor(android.R.color.darker_gray, null))
            typeface = null
        }
        findViewById<TextView>(com.example.mywellness.R.id.nav_habits_text).apply {
            setTextColor(resources.getColor(android.R.color.darker_gray, null))
            typeface = null
        }
        findViewById<TextView>(com.example.mywellness.R.id.nav_mood_text).apply {
            setTextColor(resources.getColor(android.R.color.darker_gray, null))
            typeface = null
        }
        findViewById<TextView>(com.example.mywellness.R.id.nav_profile_text).apply {
            setTextColor(resources.getColor(android.R.color.darker_gray, null))
            typeface = null
        }
        
        // Set active tab color
        when (tabName) {
            "home" -> {
                findViewById<TextView>(com.example.mywellness.R.id.nav_home_text).apply {
                    setTextColor(resources.getColor(android.R.color.holo_orange_dark, null))
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }
            }
            "habits" -> {
                findViewById<TextView>(com.example.mywellness.R.id.nav_habits_text).apply {
                    setTextColor(resources.getColor(android.R.color.holo_orange_dark, null))
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }
            }
            "mood" -> {
                findViewById<TextView>(com.example.mywellness.R.id.nav_mood_text).apply {
                    setTextColor(resources.getColor(android.R.color.holo_orange_dark, null))
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }
            }
            "profile" -> {
                findViewById<TextView>(com.example.mywellness.R.id.nav_profile_text).apply {
                    setTextColor(resources.getColor(android.R.color.holo_orange_dark, null))
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }
            }
        }
    }

    private fun showWaterTrackerPopup() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(com.example.mywellness.R.layout.water_tracker_popup)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        // Update progress text in popup
        dialog.findViewById<TextView>(com.example.mywellness.R.id.waterProgressText).text = "$currentWater/$maxWater"
        
        // Setup glass click listeners
        val glassIds = arrayOf(
            com.example.mywellness.R.id.glass1, com.example.mywellness.R.id.glass2,
            com.example.mywellness.R.id.glass3, com.example.mywellness.R.id.glass4,
            com.example.mywellness.R.id.glass5, com.example.mywellness.R.id.glass6,
            com.example.mywellness.R.id.glass7, com.example.mywellness.R.id.glass8
        )
        
        // Update glass appearances and setup click listeners
        for (i in glassIds.indices) {
            val glassFrame = dialog.findViewById<FrameLayout>(glassIds[i])
            val glassIcon = glassFrame.getChildAt(0) as TextView
            
            if (i < currentWater) {
                // Filled glass
                glassFrame.setBackgroundResource(com.example.mywellness.R.drawable.filled_glass_bg)
                glassIcon.alpha = 1.0f
            } else {
                // Empty glass
                glassFrame.setBackgroundResource(com.example.mywellness.R.drawable.empty_glass_bg)
                glassIcon.alpha = 0.4f
            }
            
            // Click listener for each glass
            glassFrame.setOnClickListener {
                if (i < currentWater) {
                    // Remove water (unfill glass)
                    currentWater = i
                    Toast.makeText(this, "Water removed 💧", Toast.LENGTH_SHORT).show()
                } else if (i == currentWater && currentWater < maxWater) {
                    // Add water (fill glass)
                    currentWater = i + 1
                    Toast.makeText(this, "Great! Stay hydrated! 💧", Toast.LENGTH_SHORT).show()
                }
                
                saveData()
                updateWaterDisplay()
                updateGlassesInPopup(dialog, glassIds)
                dialog.findViewById<TextView>(com.example.mywellness.R.id.waterProgressText).text = "$currentWater/$maxWater"
                
                if (currentWater == maxWater) {
                    Toast.makeText(this, "🎉 Daily water goal achieved!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        // Add Glass button
        dialog.findViewById<Button>(com.example.mywellness.R.id.addGlassButton).setOnClickListener {
            if (currentWater < maxWater) {
                currentWater++
                saveData()
                updateWaterDisplay()
                updateGlassesInPopup(dialog, glassIds)
                dialog.findViewById<TextView>(com.example.mywellness.R.id.waterProgressText).text = "$currentWater/$maxWater"
                Toast.makeText(this, "Great! Stay hydrated! 💧", Toast.LENGTH_SHORT).show()
                
                if (currentWater == maxWater) {
                    Toast.makeText(this, "🎉 Daily water goal achieved!", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Daily goal completed! 🎯", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Close button
        dialog.findViewById<Button>(com.example.mywellness.R.id.closeButton).setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }
    
    private fun updateGlassesInPopup(dialog: Dialog, glassIds: Array<Int>) {
        for (i in glassIds.indices) {
            val glassFrame = dialog.findViewById<FrameLayout>(glassIds[i])
            val glassIcon = glassFrame.getChildAt(0) as TextView
            
            if (i < currentWater) {
                // Filled glass
                glassFrame.setBackgroundResource(com.example.mywellness.R.drawable.filled_glass_bg)
                glassIcon.alpha = 1.0f
            } else {
                // Empty glass
                glassFrame.setBackgroundResource(com.example.mywellness.R.drawable.empty_glass_bg)
                glassIcon.alpha = 0.4f
            }
        }
    }

    private fun showMealTrackerPopup() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(com.example.mywellness.R.layout.meal_tracker_popup)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        // Update progress text in popup
        dialog.findViewById<TextView>(com.example.mywellness.R.id.mealProgressText).text = "$currentMeals/$maxMeals"
        
        // Setup meal click listeners
        val mealIds = arrayOf(
            com.example.mywellness.R.id.breakfast,
            com.example.mywellness.R.id.lunch,
            com.example.mywellness.R.id.dinner
        )
        
        val mealNames = arrayOf("Breakfast", "Lunch", "Dinner")
        val mealCompletionStatus = arrayOf(
            sharedPreferences.getBoolean("breakfast_completed", false),
            sharedPreferences.getBoolean("lunch_completed", false),
            sharedPreferences.getBoolean("dinner_completed", false)
        )
        
        // Setup click listeners for each meal
        for (i in mealIds.indices) {
            val mealFrame = dialog.findViewById<FrameLayout>(mealIds[i])
            
            // Set initial appearance
            if (mealCompletionStatus[i]) {
                mealFrame.setBackgroundResource(com.example.mywellness.R.drawable.filled_meal_bg)
            } else {
                mealFrame.setBackgroundResource(com.example.mywellness.R.drawable.empty_glass_bg)
            }
            
            // Click listener for each meal
            mealFrame.setOnClickListener {
                mealCompletionStatus[i] = !mealCompletionStatus[i]
                
                if (mealCompletionStatus[i]) {
                    mealFrame.setBackgroundResource(com.example.mywellness.R.drawable.filled_meal_bg)
                    Toast.makeText(this, "${mealNames[i]} completed! 🍽️", Toast.LENGTH_SHORT).show()
                } else {
                    mealFrame.setBackgroundResource(com.example.mywellness.R.drawable.empty_glass_bg)
                    Toast.makeText(this, "${mealNames[i]} removed", Toast.LENGTH_SHORT).show()
                }
                
                // Save meal completion status
                val mealKeys = arrayOf("breakfast_completed", "lunch_completed", "dinner_completed")
                sharedPreferences.edit().putBoolean(mealKeys[i], mealCompletionStatus[i]).apply()
                
                // Update meal count
                currentMeals = mealCompletionStatus.count { it }
                saveData()
                updateMealsDisplay()
                dialog.findViewById<TextView>(com.example.mywellness.R.id.mealProgressText).text = "$currentMeals/$maxMeals"
                
                if (currentMeals == maxMeals) {
                    Toast.makeText(this, "🎉 All meals completed for today!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        // Add Meal button (for quick completion)
        dialog.findViewById<Button>(com.example.mywellness.R.id.addMealButton).setOnClickListener {
            val incompleteMeal = mealCompletionStatus.indexOfFirst { !it }
            if (incompleteMeal != -1) {
                mealCompletionStatus[incompleteMeal] = true
                
                // Save meal completion status
                val mealKeys = arrayOf("breakfast_completed", "lunch_completed", "dinner_completed")
                sharedPreferences.edit().putBoolean(mealKeys[incompleteMeal], true).apply()
                
                currentMeals = mealCompletionStatus.count { it }
                saveData()
                updateMealsDisplay()
                
                val mealFrame = dialog.findViewById<FrameLayout>(mealIds[incompleteMeal])
                mealFrame.setBackgroundResource(com.example.mywellness.R.drawable.filled_meal_bg)
                
                dialog.findViewById<TextView>(com.example.mywellness.R.id.mealProgressText).text = "$currentMeals/$maxMeals"
                Toast.makeText(this, "${mealNames[incompleteMeal]} added! 🍽️", Toast.LENGTH_SHORT).show()
                
                if (currentMeals == maxMeals) {
                    Toast.makeText(this, "🎉 All meals completed for today!", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "All meals already completed! 🎯", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Close button
        dialog.findViewById<Button>(com.example.mywellness.R.id.closeMealButton).setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }
}
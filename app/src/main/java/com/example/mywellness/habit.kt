package com.example.mywellness

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.example.mywellness.HabitProgressManager
import androidx.cardview.widget.CardView
import com.google.android.material.floatingactionbutton.FloatingActionButton

data class Habit(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val frequency: String,
    val time: String,
    var isCompleted: Boolean = false
)

class HabitActivity : AppCompatActivity() {

    private val defaultHabits = mutableMapOf(
        "water" to false,
        "meals" to true,
        "exercise" to true,
        "reading" to true,
        "meditation" to false,
        "sleep" to true
    )
    
    private val customHabits = mutableListOf<Habit>()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var habitsContainer: LinearLayout
    private lateinit var habitProgressManager: HabitProgressManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.mywellness.R.layout.habit)

        // Hide action bar for clean UI
        supportActionBar?.hide()
        
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyWellnessHabits", MODE_PRIVATE)
        
        // Initialize HabitProgressManager
        habitProgressManager = HabitProgressManager(this)
        
        // Find the container where we'll add custom habits
        findHabitsContainer()
        
        loadCustomHabits()
        loadDefaultHabits()
        setupDefaultHabitCards()
        addCustomHabitCards()
        setupFloatingActionButton()
        setupBottomNavigation()
        updateOverallProgress()
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh habit displays and progress when returning to the page
        loadDefaultHabits()
        updateHabitDisplay("water")
        updateHabitDisplay("exercise")
        updateHabitDisplay("reading")
        updateHabitDisplay("meditation")
        updateHabitDisplay("sleep")
        updateOverallProgress()
    }

    private fun findHabitsContainer() {
        // Find the ScrollView and its LinearLayout container to add custom habits
        val scrollView = findViewById<ScrollView>(com.example.mywellness.R.id.scrollView) ?: 
                         findViewById<ScrollView>(android.R.id.content)
        
        if (scrollView != null) {
            habitsContainer = scrollView.getChildAt(0) as LinearLayout
        } else {
            // Fallback: create a container if not found
            habitsContainer = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
            }
        }
    }
    
    private fun loadCustomHabits() {
        customHabits.clear()
        val habitCount = sharedPreferences.getInt("habit_count", 0)
        
        for (i in 1..habitCount) {
            val id = sharedPreferences.getString("habit_${i}_id", "") ?: ""
            val name = sharedPreferences.getString("habit_${i}_name", "") ?: ""
            val description = sharedPreferences.getString("habit_${i}_description", "") ?: ""
            val icon = sharedPreferences.getString("habit_${i}_icon", "📚") ?: "📚"
            val frequency = sharedPreferences.getString("habit_${i}_frequency", "Daily") ?: "Daily"
            val time = sharedPreferences.getString("habit_${i}_time", "09:00 AM") ?: "09:00 AM"
            val isCompleted = sharedPreferences.getBoolean("habit_${i}_completed", false)
            
            if (id.isNotEmpty() && name.isNotEmpty()) {
                // Create habit with both name and description
                customHabits.add(Habit(id, name, description, icon, frequency, time, isCompleted))
            }
        }
    }
    
    private fun addCustomHabitCards() {
        customHabits.forEach { habit ->
            addCustomHabitCard(habit)
        }
    }
    
    private fun addCustomHabitCard(habit: Habit) {
        val cardView = LayoutInflater.from(this).inflate(R.layout.custom_habit_card, null) as CardView
        
        // Set up the card content
        val iconTextView = cardView.findViewById<TextView>(R.id.custom_habit_icon)
        val titleTextView = cardView.findViewById<TextView>(R.id.custom_habit_title)
        val timeTextView = cardView.findViewById<TextView>(R.id.custom_habit_time)
        val checkmarkTextView = cardView.findViewById<TextView>(R.id.custom_habit_checkmark)
        val moreOptionsIcon = cardView.findViewById<ImageView>(R.id.habit_more_options)
        
        iconTextView.text = habit.icon
        titleTextView.text = habit.name // Show habit name as title
        timeTextView.text = habit.description // Show description instead of time
        timeTextView.visibility = View.VISIBLE // Make description visible
        checkmarkTextView.text = if (habit.isCompleted) "✅" else "⭕"
        
        // Set click listener for habit completion
        cardView.setOnClickListener {
            habit.isCompleted = !habit.isCompleted
            checkmarkTextView.text = if (habit.isCompleted) "✅" else "⭕"
            saveCustomHabitState(habit)
            updateOverallProgress()
            showCustomToast(
                if (habit.isCompleted) "${habit.icon} ${habit.name} completed!" 
                else "${habit.name} unchecked"
            )
        }
        
        // Set click listener for more options
        moreOptionsIcon.setOnClickListener { view ->
            showHabitOptionsMenu(view, habit, cardView)
        }
        
        // Add some margin
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 0, 0, 32) // 16dp bottom margin
        cardView.layoutParams = layoutParams
        
        // Add to container before the FAB (find position to insert)
        val insertIndex = findInsertPositionForCustomHabits()
        habitsContainer.addView(cardView, insertIndex)
    }
    
    private fun findInsertPositionForCustomHabits(): Int {
        // Insert after the last default habit card but before the bottom navigation
        // This is a simple implementation - you might need to adjust based on your layout
        return habitsContainer.childCount - 1 // Insert before the last child (which should be bottom nav or similar)
    }
    
    private fun saveCustomHabitState(habit: Habit) {
        // Find the habit index and update its state
        val habitCount = sharedPreferences.getInt("habit_count", 0)
        val editor = sharedPreferences.edit()
        
        for (i in 1..habitCount) {
            val storedId = sharedPreferences.getString("habit_${i}_id", "")
            if (storedId == habit.id) {
                editor.putBoolean("habit_${i}_completed", habit.isCompleted)
                break
            }
        }
        editor.apply()
    }

    private fun loadDefaultHabits() {
        // Load saved habit states from SharedPreferences
        defaultHabits["water"] = sharedPreferences.getBoolean("drink_water", false)
        defaultHabits["meals"] = sharedPreferences.getBoolean("eat_meals", true) // Default to true as originally set
        defaultHabits["exercise"] = sharedPreferences.getBoolean("exercise", false)
        defaultHabits["reading"] = sharedPreferences.getBoolean("read_book", false)
        defaultHabits["meditation"] = sharedPreferences.getBoolean("meditate", false)
        defaultHabits["sleep"] = sharedPreferences.getBoolean("sleep", true) // Default to true as originally set
    }

    private fun setupDefaultHabitCards() {
        // Water habit
        findViewById<CardView>(com.example.mywellness.R.id.waterHabitCard).setOnClickListener {
            defaultHabits["water"] = !defaultHabits["water"]!!
            sharedPreferences.edit().putBoolean("drink_water", defaultHabits["water"]!!).apply()
            updateHabitDisplay("water")
            updateOverallProgress()
            showCustomToast(if (defaultHabits["water"]!!) "Water goal completed! 💧" else "Water goal unchecked")
        }

        // Meals habit
        findViewById<CardView>(com.example.mywellness.R.id.mealsHabitCard).setOnClickListener {
            defaultHabits["meals"] = !defaultHabits["meals"]!!
            sharedPreferences.edit().putBoolean("eat_meals", defaultHabits["meals"]!!).apply()
            updateHabitDisplay("meals")
            updateOverallProgress()
            showCustomToast(if (defaultHabits["meals"]!!) "Meals completed! 🍽️" else "Meals unchecked")
        }

        // Exercise habit
        findViewById<CardView>(com.example.mywellness.R.id.exerciseHabitCard).setOnClickListener {
            defaultHabits["exercise"] = !defaultHabits["exercise"]!!
            sharedPreferences.edit().putBoolean("exercise", defaultHabits["exercise"]!!).apply()
            updateHabitDisplay("exercise")
            updateOverallProgress()
            showCustomToast(if (defaultHabits["exercise"]!!) "Exercise completed! 🏃" else "Exercise unchecked")
        }

        // Reading habit
        findViewById<CardView>(com.example.mywellness.R.id.readingHabitCard).setOnClickListener {
            defaultHabits["reading"] = !defaultHabits["reading"]!!
            sharedPreferences.edit().putBoolean("read_book", defaultHabits["reading"]!!).apply()
            updateHabitDisplay("reading")
            updateOverallProgress()
            showCustomToast(if (defaultHabits["reading"]!!) "Reading completed! 📚" else "Reading unchecked")
        }

        // Meditation habit
        findViewById<CardView>(com.example.mywellness.R.id.meditationHabitCard).setOnClickListener {
            defaultHabits["meditation"] = !defaultHabits["meditation"]!!
            sharedPreferences.edit().putBoolean("meditate", defaultHabits["meditation"]!!).apply()
            updateHabitDisplay("meditation")
            updateOverallProgress()
            showCustomToast(if (defaultHabits["meditation"]!!) "Meditation completed! 🧘" else "Meditation unchecked")
        }

        // Sleep habit
        findViewById<CardView>(com.example.mywellness.R.id.sleepHabitCard).setOnClickListener {
            defaultHabits["sleep"] = !defaultHabits["sleep"]!!
            sharedPreferences.edit().putBoolean("sleep", defaultHabits["sleep"]!!).apply()
            updateHabitDisplay("sleep")
            updateOverallProgress()
            showCustomToast(if (defaultHabits["sleep"]!!) "Sleep goal set! 😴" else "Sleep goal unchecked")
        }

        // Initialize displays
        updateHabitDisplay("water")
        updateHabitDisplay("meals")
        updateHabitDisplay("exercise")
        updateHabitDisplay("reading")
        updateHabitDisplay("meditation")
        updateHabitDisplay("sleep")
        
        // Setup more options buttons for default habits
        setupDefaultHabitMoreOptions()
    }
    
    private fun setupDefaultHabitMoreOptions() {
        // Water habit more options
        findViewById<ImageView>(R.id.water_more_options).setOnClickListener { view ->
            showDefaultHabitOptionsMenu(view, "water", "💧", "Drink 8 Glasses of Water")
        }
        
        // Meals habit more options
        findViewById<ImageView>(R.id.meals_more_options).setOnClickListener { view ->
            showDefaultHabitOptionsMenu(view, "meals", "🍽️", "Eat 3 Main Meals")
        }
        
        // Exercise habit more options
        findViewById<ImageView>(R.id.exercise_more_options).setOnClickListener { view ->
            showDefaultHabitOptionsMenu(view, "exercise", "🏃", "30 Minutes Exercise")
        }
        
        // Reading habit more options
        findViewById<ImageView>(R.id.reading_more_options).setOnClickListener { view ->
            showDefaultHabitOptionsMenu(view, "reading", "📚", "Read for 20 Minutes")
        }
        
        // Meditation habit more options
        findViewById<ImageView>(R.id.meditation_more_options).setOnClickListener { view ->
            showDefaultHabitOptionsMenu(view, "meditation", "🧘", "10 Minutes Meditation")
        }
        
        // Sleep habit more options
        findViewById<ImageView>(R.id.sleep_more_options).setOnClickListener { view ->
            showDefaultHabitOptionsMenu(view, "sleep", "😴", "8 Hours Quality Sleep")
        }
    }

    private fun updateHabitDisplay(habitType: String) {
        val isCompleted = defaultHabits[habitType] ?: false
        val checkmark = if (isCompleted) "✅" else "⭕"

        when (habitType) {
            "water" -> {
                findViewById<TextView>(com.example.mywellness.R.id.waterCheckmark).text = checkmark
                if (isCompleted) {
                    findViewById<TextView>(com.example.mywellness.R.id.waterProgress).text = "8/8 glasses completed"
                } else {
                    findViewById<TextView>(com.example.mywellness.R.id.waterProgress).text = "6/8 glasses completed"
                }
            }
            "meals" -> {
                findViewById<TextView>(com.example.mywellness.R.id.mealsCheckmark).text = checkmark
                if (isCompleted) {
                    findViewById<TextView>(com.example.mywellness.R.id.mealsProgress).text = "3/3 meals completed"
                } else {
                    findViewById<TextView>(com.example.mywellness.R.id.mealsProgress).text = "2/3 meals completed"
                }
            }
            "exercise" -> findViewById<TextView>(com.example.mywellness.R.id.exerciseCheckmark).text = checkmark
            "reading" -> findViewById<TextView>(com.example.mywellness.R.id.readingCheckmark).text = checkmark
            "meditation" -> findViewById<TextView>(com.example.mywellness.R.id.meditationCheckmark).text = checkmark
            "sleep" -> findViewById<TextView>(com.example.mywellness.R.id.sleepCheckmark).text = checkmark
        }
    }

    private fun updateOverallProgress() {
        // Count completed default habits
        val completedDefaultCount = defaultHabits.values.count { it }
        
        // Count completed custom habits
        val completedCustomCount = customHabits.count { it.isCompleted }
        
        val totalCompletedCount = completedDefaultCount + completedCustomCount
        val totalHabitCount = defaultHabits.size + customHabits.size
        
        val percentage = if (totalHabitCount > 0) {
            (totalCompletedCount * 100) / totalHabitCount
        } else {
            0
        }

        findViewById<TextView>(com.example.mywellness.R.id.overallProgress).text = "$totalCompletedCount/$totalHabitCount Complete"

        // Update progress bar
        val progressView = findViewById<android.view.View>(com.example.mywellness.R.id.progressBar)
        val layoutParams = progressView.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = percentage.toFloat()
        progressView.layoutParams = layoutParams

        // Update the remaining part
        val progressParent = progressView.parent as LinearLayout
        if (progressParent.childCount > 1) {
            val remainingView = progressParent.getChildAt(1)
            val remainingParams = remainingView.layoutParams as LinearLayout.LayoutParams
            remainingParams.weight = (100 - percentage).toFloat()
            remainingView.layoutParams = remainingParams
        }
        
        // Update HabitProgressManager for real-time sync with home page
        habitProgressManager.updateHabitProgress()
    }

    private fun setupFloatingActionButton() {
        findViewById<FloatingActionButton>(com.example.mywellness.R.id.fab_add_habit).setOnClickListener {
            val intent = Intent(this, AddNewHabitActivity::class.java)
            startActivityForResult(intent, ADD_HABIT_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == ADD_HABIT_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.let {
                val habitAdded = it.getBooleanExtra("habit_added", false)
                
                if (habitAdded) {
                    // Reload custom habits and refresh the UI
                    loadCustomHabits()
                    refreshCustomHabits()
                    updateOverallProgress()
                    
                    val habitDescription = it.getStringExtra("habit_description")
                    val habitIcon = it.getStringExtra("habit_icon")
                    
                    showCustomToast("New habit added: $habitIcon $habitDescription")
                }
            }
        }
    }
    
    private fun refreshCustomHabits() {
        // Remove all custom habit cards first
        val cardsToRemove = mutableListOf<CardView>()
        for (i in 0 until habitsContainer.childCount) {
            val child = habitsContainer.getChildAt(i)
            if (child.findViewById<TextView>(R.id.custom_habit_icon) != null) {
                cardsToRemove.add(child as CardView)
            }
        }
        cardsToRemove.forEach { habitsContainer.removeView(it) }
        
        // Add all custom habits again
        addCustomHabitCards()
    }

    companion object {
        private const val ADD_HABIT_REQUEST_CODE = 1001
    }

    private fun setupBottomNavigation() {
        // Home navigation
        findViewById<LinearLayout>(com.example.mywellness.R.id.nav_home).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        // Habits navigation (current page)
        findViewById<LinearLayout>(com.example.mywellness.R.id.nav_habits).setOnClickListener {
            showCustomToast("You're already on Habits! ✅")
        }

        // Mood navigation
        findViewById<LinearLayout>(com.example.mywellness.R.id.nav_mood).setOnClickListener {
            startActivity(Intent(this, MoodActivity::class.java))
            finish()
        }

        // Profile navigation
        findViewById<LinearLayout>(com.example.mywellness.R.id.nav_profile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }
    
    private fun showHabitOptionsMenu(view: View, habit: Habit, cardView: CardView) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.habit_options_menu, popup.menu)
        
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_update_habit -> {
                    updateHabit(habit)
                    true
                }
                R.id.menu_delete_habit -> {
                    showDeleteConfirmationDialog(habit, cardView)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }
    
    private fun showDefaultHabitOptionsMenu(view: View, habitKey: String, habitIcon: String, habitName: String) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.habit_options_menu, popup.menu)
        
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_update_habit -> {
                    updateDefaultHabit(habitKey, habitIcon, habitName)
                    true
                }
                R.id.menu_delete_habit -> {
                    showDeleteDefaultHabitConfirmationDialog(habitKey, habitIcon, habitName)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }
    
    private fun updateDefaultHabit(habitKey: String, habitIcon: String, habitName: String) {
        val intent = Intent(this, AddNewHabitActivity::class.java)
        intent.putExtra("habit_id", "default_$habitKey")
        intent.putExtra("habit_name", habitName)
        intent.putExtra("habit_description", habitName)
        intent.putExtra("habit_icon", habitIcon)
        intent.putExtra("habit_frequency", "Daily")
        intent.putExtra("habit_time", "09:00 AM")
        intent.putExtra("is_update", true)
        intent.putExtra("is_default_habit", true)
        intent.putExtra("default_habit_key", habitKey)
        startActivityForResult(intent, 1001)
    }
    
    private fun showDeleteDefaultHabitConfirmationDialog(habitKey: String, habitIcon: String, habitName: String) {
        AlertDialog.Builder(this)
            .setTitle("Reset Habit")
            .setMessage("Are you sure you want to reset '$habitName' habit? This will uncheck the habit but keep it in your list.")
            .setPositiveButton("Reset") { _, _ ->
                resetDefaultHabit(habitKey)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun resetDefaultHabit(habitKey: String) {
        defaultHabits[habitKey] = false
        updateHabitDisplay(habitKey)
        updateOverallProgress()
        showCustomToast("Habit reset successfully")
    }
    
    private fun updateHabit(habit: Habit) {
        val intent = Intent(this, AddNewHabitActivity::class.java)
        intent.putExtra("habit_id", habit.id)
        intent.putExtra("habit_name", habit.name)
        intent.putExtra("habit_description", habit.description)
        intent.putExtra("habit_icon", habit.icon)
        intent.putExtra("habit_frequency", habit.frequency)
        intent.putExtra("habit_time", habit.time)
        intent.putExtra("is_update", true)
        startActivityForResult(intent, 1001)
    }
    
    private fun showDeleteConfirmationDialog(habit: Habit, cardView: CardView) {
        AlertDialog.Builder(this)
            .setTitle("Delete Habit")
            .setMessage("Are you sure you want to delete '${habit.name}' habit?")
            .setPositiveButton("Delete") { _, _ ->
                deleteHabit(habit, cardView)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun deleteHabit(habit: Habit, cardView: CardView) {
        // Remove from customHabits list
        customHabits.removeAll { it.id == habit.id }
        
        // Remove from UI
        habitsContainer.removeView(cardView)
        
        // Update SharedPreferences
        deleteHabitFromPreferences(habit.id)
        
        // Update progress
        updateOverallProgress()
        
        // Show confirmation
        showCustomToast("${habit.icon} ${habit.description} deleted")
    }
    
    private fun deleteHabitFromPreferences(habitId: String) {
        val editor = sharedPreferences.edit()
        val habitCount = sharedPreferences.getInt("habit_count", 0)
        
        // Find and remove the specific habit
        var foundIndex = -1
        for (i in 1..habitCount) {
            val id = sharedPreferences.getString("habit_${i}_id", "")
            if (id == habitId) {
                foundIndex = i
                break
            }
        }
        
        if (foundIndex != -1) {
            // Shift all habits after the deleted one
            for (i in foundIndex until habitCount) {
                val nextIndex = i + 1
                if (nextIndex <= habitCount) {
                    // Copy next habit data to current position
                    val nextId = sharedPreferences.getString("habit_${nextIndex}_id", "")
                    val nextName = sharedPreferences.getString("habit_${nextIndex}_name", "")
                    val nextDescription = sharedPreferences.getString("habit_${nextIndex}_description", "")
                    val nextIcon = sharedPreferences.getString("habit_${nextIndex}_icon", "")
                    val nextFrequency = sharedPreferences.getString("habit_${nextIndex}_frequency", "")
                    val nextTime = sharedPreferences.getString("habit_${nextIndex}_time", "")
                    val nextCompleted = sharedPreferences.getBoolean("habit_${nextIndex}_completed", false)
                    
                    editor.putString("habit_${i}_id", nextId)
                    editor.putString("habit_${i}_name", nextName)
                    editor.putString("habit_${i}_description", nextDescription)
                    editor.putString("habit_${i}_icon", nextIcon)
                    editor.putString("habit_${i}_frequency", nextFrequency)
                    editor.putString("habit_${i}_time", nextTime)
                    editor.putBoolean("habit_${i}_completed", nextCompleted)
                }
            }
            
            // Remove the last habit entry
            editor.remove("habit_${habitCount}_id")
            editor.remove("habit_${habitCount}_name")
            editor.remove("habit_${habitCount}_description")
            editor.remove("habit_${habitCount}_icon")
            editor.remove("habit_${habitCount}_frequency")
            editor.remove("habit_${habitCount}_time")
            editor.remove("habit_${habitCount}_completed")
            
            // Update habit count
            editor.putInt("habit_count", habitCount - 1)
        }
        
        editor.apply()
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
}
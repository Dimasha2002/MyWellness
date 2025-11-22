package com.example.mywellness

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.text.SimpleDateFormat
import java.util.*

class MoodActivity : AppCompatActivity() {
    
    private var currentMood = "Happy"
    private var currentMoodEmoji = "😊"
    private lateinit var sharedPreferences: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.mywellness.R.layout.mood)
        
        // Hide action bar for clean UI
        supportActionBar?.hide()
        
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyWellnessMood", MODE_PRIVATE)
        
        // Check and handle new week
        checkAndHandleNewWeek()
        
        // Clear previous two weeks mood data
        clearPreviousTwoWeeksMoodData()
        
        setupMoodSelection()
        setupBottomNavigation()
        loadTodaysMood()
        updateCurrentMoodDisplay()
        updateWeeklyMoodDisplay()
        updatePreviousWeeksDates()
        updatePreviousWeeksData()
    }
    
    private fun setupMoodSelection() {
        // Happy mood
        findViewById<CardView>(com.example.mywellness.R.id.moodHappy).setOnClickListener {
            setMood("Happy", "😊")
            Toast.makeText(this, "Mood set to Happy! 😊", Toast.LENGTH_SHORT).show()
        }
        
        // Excited mood
        findViewById<CardView>(com.example.mywellness.R.id.moodExcited).setOnClickListener {
            setMood("Excited", "🤩")
            Toast.makeText(this, "Mood set to Excited! 🤩", Toast.LENGTH_SHORT).show()
        }
        
        // Calm mood
        findViewById<CardView>(com.example.mywellness.R.id.moodCalm).setOnClickListener {
            setMood("Calm", "😌")
            Toast.makeText(this, "Mood set to Calm! 😌", Toast.LENGTH_SHORT).show()
        }
        
        // Neutral mood
        findViewById<CardView>(com.example.mywellness.R.id.moodNeutral).setOnClickListener {
            setMood("Neutral", "😐")
            Toast.makeText(this, "Mood set to Neutral! 😐", Toast.LENGTH_SHORT).show()
        }
        
        // Tired mood
        findViewById<CardView>(com.example.mywellness.R.id.moodTired).setOnClickListener {
            setMood("Tired", "😴")
            Toast.makeText(this, "Mood set to Tired! 😴", Toast.LENGTH_SHORT).show()
        }
        
        // Sad mood
        findViewById<CardView>(com.example.mywellness.R.id.moodSad).setOnClickListener {
            setMood("Sad", "😢")
            Toast.makeText(this, "Mood set to Sad! 😢", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setMood(mood: String, emoji: String) {
        currentMood = mood
        currentMoodEmoji = emoji
        
        // Save mood to SharedPreferences with current date
        saveMoodToPreferences(mood, emoji)
        
        // Update current mood display first
        updateCurrentMoodDisplay()
        
        // Force reload today's mood and update weekly display
        loadTodaysMood()
        updateWeeklyMoodDisplay()
        updatePreviousWeeksData()
        
        // Show encouraging message based on mood
        when (mood) {
            "Happy" -> Toast.makeText(this, "Great to see you happy! Keep shining! ✨", Toast.LENGTH_LONG).show()
            "Excited" -> Toast.makeText(this, "Your excitement is contagious! 🎉", Toast.LENGTH_LONG).show()
            "Calm" -> Toast.makeText(this, "Peace and tranquility. Take a deep breath! 🌸", Toast.LENGTH_LONG).show()
            "Neutral" -> Toast.makeText(this, "Every day is a new opportunity! 🌅", Toast.LENGTH_LONG).show()
            "Tired" -> Toast.makeText(this, "Rest well, you deserve it! Remember to recharge! 💤", Toast.LENGTH_LONG).show()
            "Sad" -> Toast.makeText(this, "It's okay to feel sad. Tomorrow is a new day! 🌈", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun saveMoodToPreferences(mood: String, emoji: String) {
        val tz = TimeZone.getTimeZone("Asia/Colombo")
        val today = Calendar.getInstance(tz)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormat.timeZone = tz
        val dateKey = dateFormat.format(today.time)
        val editor = sharedPreferences.edit()
        editor.putString("mood_$dateKey", mood)
        editor.putString("emoji_$dateKey", emoji)
        editor.apply()
    }
    
    private fun loadTodaysMood() {
        val tz = TimeZone.getTimeZone("Asia/Colombo")
        val today = Calendar.getInstance(tz)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormat.timeZone = tz
        val dateKey = dateFormat.format(today.time)
        currentMood = sharedPreferences.getString("mood_$dateKey", "Happy") ?: "Happy"
        currentMoodEmoji = sharedPreferences.getString("emoji_$dateKey", "😊") ?: "😊"
    }
    
    private fun updateCurrentMoodDisplay() {
        findViewById<TextView>(com.example.mywellness.R.id.currentMoodEmoji).text = currentMoodEmoji
        findViewById<TextView>(com.example.mywellness.R.id.currentMoodText).text = currentMood
    }
    
    private fun fillRandomPreviousDaysMoods() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = calendar.get(Calendar.DAY_OF_WEEK)
        
        // Available moods and emojis
        val moods = arrayOf("Happy", "Excited", "Calm", "Grateful", "Sad")
        val emojis = arrayOf("😊", "🤩", "😌", "🙏", "😢")
        
        // Set to start of current week (Monday)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        
        // Determine which day index corresponds to today (Saturday = 5)
        val todayDayIndex = when (today) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> 5 // Default to Saturday
        }
        
        // Fill random moods for previous days only
        for (i in 0 until todayDayIndex) {
            val dateKey = dateFormat.format(calendar.time)
            
            // Check if mood is already set for this day
            val existingMood = sharedPreferences.getString("mood_$dateKey", null)
            
            if (existingMood == null) {
                // Generate random mood for this previous day
                val randomIndex = (0 until moods.size).random()
                val randomMood = moods[randomIndex]
                val randomEmoji = emojis[randomIndex]
                
                // Save the random mood
                val editor = sharedPreferences.edit()
                editor.putString("mood_$dateKey", randomMood)
                editor.putString("emoji_$dateKey", randomEmoji)
                editor.apply()
            }
            
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }
    
    private fun setMondayMoodToHappy() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        // Set to start of current week (Monday)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val mondayDateKey = dateFormat.format(calendar.time)
        
        // Set Monday's mood to Happy
        val editor = sharedPreferences.edit()
        editor.putString("mood_$mondayDateKey", "Happy")
        editor.putString("emoji_$mondayDateKey", "😊")
        editor.apply()
    }
    
    private fun checkAndHandleNewWeek() {
        val tz = TimeZone.getTimeZone("Asia/Colombo")
        val calendar = Calendar.getInstance(tz)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormat.timeZone = tz
        
        // Get the last week number we stored
        val lastWeekNumber = sharedPreferences.getInt("last_week_number", -1)
        val currentWeekNumber = calendar.get(Calendar.WEEK_OF_YEAR)
        
        if (lastWeekNumber == -1 || currentWeekNumber > lastWeekNumber) {
            // Clear all mood data for the week
            clearWeekMoodData(calendar)
            
            // Update the week number
            sharedPreferences.edit().putInt("last_week_number", currentWeekNumber).apply()
            
            // Set current day's mood if not already set
            setInitialMoodForToday()
        }
    }
    
    private fun clearWeekMoodData(calendar: Calendar) {
        val editor = sharedPreferences.edit()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormat.timeZone = calendar.timeZone
        
        // Save current calendar state
        val savedTime = calendar.timeInMillis
        
        // Set to start of week (Monday)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        
        // Clear entire week
        for (i in 0..6) {
            val dateKey = dateFormat.format(calendar.time)
            editor.remove("mood_$dateKey")
            editor.remove("emoji_$dateKey")
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        
        editor.apply()
        
        // Restore calendar to original state
        calendar.timeInMillis = savedTime
    }
    
    private fun setInitialMoodForToday() {
        val tz = TimeZone.getTimeZone("Asia/Colombo")
        val today = Calendar.getInstance(tz)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormat.timeZone = tz
        val dateKey = dateFormat.format(today.time)
        
        // Only set if no mood exists for today
        if (sharedPreferences.getString("mood_$dateKey", null) == null) {
            val editor = sharedPreferences.edit()
            editor.putString("mood_$dateKey", "Happy")
            editor.putString("emoji_$dateKey", "😊")
            editor.apply()
        }
    }
    
    private fun updateWeeklyMoodDisplay() {
        val tz = TimeZone.getTimeZone("Asia/Colombo")
        val calendar = Calendar.getInstance(tz)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormat.timeZone = tz
        
        // Get today's date in Sri Lanka timezone
        val todayCalendar = Calendar.getInstance(tz)
        val todayDateKey = dateFormat.format(todayCalendar.time)
        
        // Calculate today's index (0 = Monday, 6 = Sunday)
        val todayDayIndex = when (todayCalendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> -1
        }
        
        // Set calendar to start of week (Monday)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val dayMoodEmojis = arrayOf(
            com.example.mywellness.R.id.mondayMoodEmoji,
            com.example.mywellness.R.id.tuesdayMoodEmoji,
            com.example.mywellness.R.id.wednesdayMoodEmoji,
            com.example.mywellness.R.id.thursdayMoodEmoji,
            com.example.mywellness.R.id.fridayMoodEmoji,
            com.example.mywellness.R.id.saturdayMoodEmoji,
            com.example.mywellness.R.id.sundayMoodEmoji
        )
        val dayLabelIds = arrayOf(
            com.example.mywellness.R.id.mondayLabel,
            com.example.mywellness.R.id.tuesdayLabel,
            com.example.mywellness.R.id.wednesdayLabel,
            com.example.mywellness.R.id.thursdayLabel,
            com.example.mywellness.R.id.fridayLabel,
            com.example.mywellness.R.id.saturdayLabel,
            com.example.mywellness.R.id.sundayLabel
        )
        // Reset all label colors first
        dayLabelIds.forEach { labelId ->
            findViewById<TextView>(labelId).apply {
                setTextColor(0xFF8B8B8B.toInt())
                setTypeface(null, android.graphics.Typeface.NORMAL)
            }
        }

        // Display moods for each day
        for (i in 0..6) {
            val dateKey = dateFormat.format(calendar.time)
            val savedEmoji = sharedPreferences.getString("emoji_$dateKey", null)
            val moodEmojiView = findViewById<TextView>(dayMoodEmojis[i])
            
            if (i == todayDayIndex && currentMoodEmoji != "😊") {
                // If it's today and we have a non-default mood, show current mood
                moodEmojiView.text = currentMoodEmoji
                moodEmojiView.setTextColor(0xFF2C2C54.toInt())
            } else if (savedEmoji != null) {
                // Show saved mood for other days
                moodEmojiView.text = savedEmoji
                moodEmojiView.setTextColor(0xFF2C2C54.toInt())
            } else if (dateKey <= todayDateKey) {
                // Show dash for past days with no mood
                moodEmojiView.text = "—"
                moodEmojiView.setTextColor(0xFFE0E0E0.toInt())
            } else {
                // Show dash for future days
                moodEmojiView.text = "—"
                moodEmojiView.setTextColor(0xFFE0E0E0.toInt())
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Highlight today's label based on the calculated todayDayIndex
        if (todayDayIndex != -1) {
            val todayLabel = findViewById<TextView>(dayLabelIds[todayDayIndex])
            todayLabel.setTextColor(0xFFF59E0B.toInt()) // Orange color
            todayLabel.setTypeface(null, android.graphics.Typeface.BOLD)
        }
    }
    
    private fun highlightTodayLabel(dayIndex: Int) {
        // Reset all day labels to normal color first
        resetDayLabels()
        
        // Array of day label IDs
        val dayLabelIds = arrayOf(
            com.example.mywellness.R.id.mondayLabel,
            com.example.mywellness.R.id.tuesdayLabel,
            com.example.mywellness.R.id.wednesdayLabel,
            com.example.mywellness.R.id.thursdayLabel,
            com.example.mywellness.R.id.fridayLabel,
            com.example.mywellness.R.id.saturdayLabel,
            com.example.mywellness.R.id.sundayLabel
        )
        
        // Highlight today's label with orange color and bold style
        val todayLabel = findViewById<TextView>(dayLabelIds[dayIndex])
        todayLabel.setTextColor(0xFFF59E0B.toInt()) // Orange color
        todayLabel.setTypeface(null, android.graphics.Typeface.BOLD)
    }
    
    private fun resetDayLabels() {
        // Array of day label IDs
        val dayLabelIds = arrayOf(
            com.example.mywellness.R.id.mondayLabel,
            com.example.mywellness.R.id.tuesdayLabel,
            com.example.mywellness.R.id.wednesdayLabel,
            com.example.mywellness.R.id.thursdayLabel,
            com.example.mywellness.R.id.fridayLabel,
            com.example.mywellness.R.id.saturdayLabel,
            com.example.mywellness.R.id.sundayLabel
        )
        
        // Reset all day labels to default gray color and normal style
        for (labelId in dayLabelIds) {
            val labelView = findViewById<TextView>(labelId)
            labelView.setTextColor(0xFF8B8B8B.toInt()) // Default gray color
            labelView.setTypeface(null, android.graphics.Typeface.NORMAL)
        }
    }
    
    private fun clearPreviousTwoWeeksMoodData() {
        clearPreviousWeekMoodData()
        clearTwoWeeksAgoMoodData()
    }
    
    private fun clearPreviousWeekMoodData() {
        val tz = TimeZone.getTimeZone("Asia/Colombo")
        val calendar = Calendar.getInstance(tz)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormat.timeZone = tz
        
        // Go to previous week
        calendar.add(Calendar.WEEK_OF_YEAR, -1)
        
        // Set to Monday of previous week
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        
        val editor = sharedPreferences.edit()
        
        // Clear entire previous week (7 days)
        for (i in 0..6) {
            val dateKey = dateFormat.format(calendar.time)
            editor.remove("mood_$dateKey")
            editor.remove("emoji_$dateKey")
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        
        editor.apply()
    }
    
    private fun clearTwoWeeksAgoMoodData() {
        val tz = TimeZone.getTimeZone("Asia/Colombo")
        val calendar = Calendar.getInstance(tz)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormat.timeZone = tz
        
        // Go to two weeks ago
        calendar.add(Calendar.WEEK_OF_YEAR, -2)
        
        // Set to Monday of two weeks ago
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        
        val editor = sharedPreferences.edit()
        
        // Clear entire two weeks ago week (7 days)
        for (i in 0..6) {
            val dateKey = dateFormat.format(calendar.time)
            editor.remove("mood_$dateKey")
            editor.remove("emoji_$dateKey")
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        
        editor.apply()
    }
    
    private fun updatePreviousWeeksData() {
        updatePreviousWeekMoodDisplay()
        updateTwoWeeksAgoMoodDisplay()
    }
    
    private fun updatePreviousWeekMoodDisplay() {
        val tz = TimeZone.getTimeZone("Asia/Colombo")
        val calendar = Calendar.getInstance(tz)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormat.timeZone = tz
        
        // Go to previous week
        calendar.add(Calendar.WEEK_OF_YEAR, -1)
        
        // Set to Monday of previous week
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        
        val prevWeekMoodEmojis = arrayOf(
            com.example.mywellness.R.id.prevMondayMoodEmoji,
            com.example.mywellness.R.id.prevTuesdayMoodEmoji,
            com.example.mywellness.R.id.prevWednesdayMoodEmoji,
            com.example.mywellness.R.id.prevThursdayMoodEmoji,
            com.example.mywellness.R.id.prevFridayMoodEmoji,
            com.example.mywellness.R.id.prevSaturdayMoodEmoji,
            com.example.mywellness.R.id.prevSundayMoodEmoji
        )
        
        // Display mood for each day of previous week
        for (i in 0..6) {
            val dateKey = dateFormat.format(calendar.time)
            val savedEmoji = sharedPreferences.getString("emoji_$dateKey", null)
            val moodEmojiView = findViewById<TextView>(prevWeekMoodEmojis[i])
            
            if (savedEmoji != null) {
                moodEmojiView.text = savedEmoji
                moodEmojiView.setTextColor(0xFF2C2C54.toInt())
            } else {
                moodEmojiView.text = "—"
                moodEmojiView.setTextColor(0xFFE0E0E0.toInt())
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }
    
    private fun updateTwoWeeksAgoMoodDisplay() {
        val tz = TimeZone.getTimeZone("Asia/Colombo")
        val calendar = Calendar.getInstance(tz)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormat.timeZone = tz
        
        // Go to two weeks ago
        calendar.add(Calendar.WEEK_OF_YEAR, -2)
        
        // Set to Monday of two weeks ago
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        
        val twoWeeksAgoMoodEmojis = arrayOf(
            com.example.mywellness.R.id.twoWeeksAgoMondayMoodEmoji,
            com.example.mywellness.R.id.twoWeeksAgoTuesdayMoodEmoji,
            com.example.mywellness.R.id.twoWeeksAgoWednesdayMoodEmoji,
            com.example.mywellness.R.id.twoWeeksAgoThursdayMoodEmoji,
            com.example.mywellness.R.id.twoWeeksAgoFridayMoodEmoji,
            com.example.mywellness.R.id.twoWeeksAgoSaturdayMoodEmoji,
            com.example.mywellness.R.id.twoWeeksAgoSundayMoodEmoji
        )
        
        // Display mood for each day of two weeks ago
        for (i in 0..6) {
            val dateKey = dateFormat.format(calendar.time)
            val savedEmoji = sharedPreferences.getString("emoji_$dateKey", null)
            val moodEmojiView = findViewById<TextView>(twoWeeksAgoMoodEmojis[i])
            
            if (savedEmoji != null) {
                moodEmojiView.text = savedEmoji
                moodEmojiView.setTextColor(0xFF2C2C54.toInt())
            } else {
                moodEmojiView.text = "—"
                moodEmojiView.setTextColor(0xFFE0E0E0.toInt())
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }
    
    private fun updatePreviousWeeksDates() {
        val tz = TimeZone.getTimeZone("Asia/Colombo")
        val today = Calendar.getInstance(tz)
        
        // Update previous week date range
        val previousWeekRange = getPreviousWeekDateRange(today)
        findViewById<TextView>(com.example.mywellness.R.id.previousWeekDateRange)?.text = previousWeekRange
        
        // Update two weeks ago date range
        val twoWeeksAgoRange = getTwoWeeksAgoDateRange(today)
        findViewById<TextView>(com.example.mywellness.R.id.twoWeeksAgoDateRange)?.text = twoWeeksAgoRange
    }
    
    private fun getPreviousWeekDateRange(currentDate: Calendar): String {
        val calendar = Calendar.getInstance(currentDate.timeZone)
        calendar.timeInMillis = currentDate.timeInMillis
        
        // Go to previous week
        calendar.add(Calendar.WEEK_OF_YEAR, -1)
        
        // Set to Monday of that week
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val startDate = calendar.time
        
        // Set to Sunday of that week
        calendar.add(Calendar.DAY_OF_MONTH, 6)
        val endDate = calendar.time
        
        val dateFormat = SimpleDateFormat("MMMM d", Locale.getDefault())
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        
        return "${dateFormat.format(startDate)} - ${dateFormat.format(endDate)}, ${yearFormat.format(endDate)}"
    }
    
    private fun getTwoWeeksAgoDateRange(currentDate: Calendar): String {
        val calendar = Calendar.getInstance(currentDate.timeZone)
        calendar.timeInMillis = currentDate.timeInMillis
        
        // Go to two weeks ago
        calendar.add(Calendar.WEEK_OF_YEAR, -2)
        
        // Set to Monday of that week
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val startDate = calendar.time
        
        // Set to Sunday of that week
        calendar.add(Calendar.DAY_OF_MONTH, 6)
        val endDate = calendar.time
        
        val dateFormat = SimpleDateFormat("MMMM d", Locale.getDefault())
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        
        return "${dateFormat.format(startDate)} - ${dateFormat.format(endDate)}, ${yearFormat.format(endDate)}"
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
        
        // Mood navigation (current page)
        findViewById<LinearLayout>(com.example.mywellness.R.id.nav_mood).setOnClickListener {
            Toast.makeText(this, "You're already on Mood! 😊", Toast.LENGTH_SHORT).show()
        }
        
        // Profile navigation
        findViewById<LinearLayout>(com.example.mywellness.R.id.nav_profile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }
}

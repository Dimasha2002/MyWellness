package com.example.mywellness

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import java.util.concurrent.TimeUnit

class NotificationManagerHelper(private val context: Context) {
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val sharedPreferences = context.getSharedPreferences("MyWellnessUser", Context.MODE_PRIVATE)
    
    companion object {
        const val HYDRATION_CHANNEL_ID = "hydration_reminders"
        const val MEAL_CHANNEL_ID = "meal_reminders"
        const val HYDRATION_WORK_NAME = "hydration_reminder_work"
        const val MEAL_WORK_NAME = "meal_reminder_work"
        const val HYDRATION_NOTIFICATION_ID = 1001
        const val MEAL_NOTIFICATION_ID = 1002
    }
    
    init {
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Hydration Channel
            val hydrationChannel = NotificationChannel(
                HYDRATION_CHANNEL_ID,
                "Hydration Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders to stay hydrated throughout the day"
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }
            
            // Meal Channel
            val mealChannel = NotificationChannel(
                MEAL_CHANNEL_ID,
                "Meal Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for meal times"
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }
            
            notificationManager.createNotificationChannel(hydrationChannel)
            notificationManager.createNotificationChannel(mealChannel)
        }
    }
    
    fun scheduleHydrationReminders(intervalSeconds: Int) {
        // Cancel any existing hydration reminders
        cancelHydrationReminders()
        
        // Store the reminder scheduling time and interval
        val currentTime = System.currentTimeMillis()
        val editor = sharedPreferences.edit()
        editor.putLong("last_water_reminder", currentTime)
        editor.putLong("water_reminder_scheduled_time", currentTime)
        editor.putInt("water_interval_seconds", intervalSeconds)
        editor.apply()
        
        // For very short intervals (< 15 minutes), use more precise timing
        val flexInterval = if (intervalSeconds < 900) { // 15 minutes
            minOf(intervalSeconds / 10, 5).toLong() // Max 5 second flex for short intervals
        } else {
            minOf(intervalSeconds / 4, 15).toLong() // Max 15 second flex for longer intervals
        }
        
        // For intervals less than 15 minutes, use OneTimeWorkRequest with repeating
        if (intervalSeconds < 900) { // Less than 15 minutes
            val workRequest = OneTimeWorkRequestBuilder<HydrationReminderWorker>()
                .setInitialDelay(intervalSeconds.toLong(), TimeUnit.SECONDS)
                .addTag("WATER_REMINDER")
                .build()
            
            WorkManager.getInstance(context).enqueueUniqueWork(
                HYDRATION_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        } else {
            // Use PeriodicWorkRequest for longer intervals (15+ minutes)
            val workRequest = PeriodicWorkRequestBuilder<HydrationReminderWorker>(
                intervalSeconds.toLong(), TimeUnit.SECONDS,
                flexInterval, TimeUnit.SECONDS
            )
                .setInitialDelay(intervalSeconds.toLong(), TimeUnit.SECONDS)
                .addTag("WATER_REMINDER")
                .build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                HYDRATION_WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
        }
    }
    
    fun scheduleMealReminders(intervalSeconds: Int) {
        // Cancel any existing meal reminders
        cancelMealReminders()
        
        // Store the meal reminder scheduling time and interval
        val currentTime = System.currentTimeMillis()
        val editor = sharedPreferences.edit()
        editor.putLong("meal_reminder_scheduled_time", currentTime)
        editor.putInt("meal_interval_seconds", intervalSeconds)
        editor.apply()
        
        // For very short intervals (< 15 minutes), use more precise timing
        val flexInterval = if (intervalSeconds < 900) { // 15 minutes
            minOf(intervalSeconds / 10, 5).toLong() // Max 5 second flex for short intervals
        } else {
            minOf(intervalSeconds / 4, 15).toLong() // Max 15 second flex for longer intervals
        }
        
        // For intervals less than 15 minutes, use OneTimeWorkRequest with repeating
        if (intervalSeconds < 900) { // Less than 15 minutes
            val workRequest = OneTimeWorkRequestBuilder<MealReminderWorker>()
                .setInitialDelay(intervalSeconds.toLong(), TimeUnit.SECONDS)
                .addTag("MEAL_REMINDER")
                .build()
            
            WorkManager.getInstance(context).enqueueUniqueWork(
                MEAL_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        } else {
            // Use PeriodicWorkRequest for longer intervals (15+ minutes)
            val workRequest = PeriodicWorkRequestBuilder<MealReminderWorker>(
                intervalSeconds.toLong(), TimeUnit.SECONDS,
                flexInterval, TimeUnit.SECONDS
            )
                .setInitialDelay(intervalSeconds.toLong(), TimeUnit.SECONDS)
                .addTag("MEAL_REMINDER")
                .build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                MEAL_WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
        }
    }
    
    fun cancelHydrationReminders() {
        WorkManager.getInstance(context).cancelUniqueWork(HYDRATION_WORK_NAME)
    }
    
    fun cancelMealReminders() {
        WorkManager.getInstance(context).cancelUniqueWork(MEAL_WORK_NAME)
    }
    
    fun showHydrationNotification() {
        val currentTime = System.currentTimeMillis()
        val scheduledTime = sharedPreferences.getLong("water_reminder_scheduled_time", currentTime)
        val intervalSeconds = sharedPreferences.getInt("water_interval_seconds", 3600)
        val lastReminderTime = sharedPreferences.getLong("last_water_reminder", currentTime)
        
        // Calculate actual interval elapsed
        val actualInterval = (currentTime - lastReminderTime) / 1000
        
        val intent = Intent(context, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Create notification with timing info
        val intervalText = sharedPreferences.getString("water_interval_display", "")
        val notificationText = if (intervalText != null && intervalText.isNotEmpty()) {
            "Your $intervalText reminder is complete! Time to drink water."
        } else {
            "Time to drink some water and stay healthy!"
        }
        
        val notification = NotificationCompat.Builder(context, HYDRATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("💧 Stay Hydrated!")
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()
        
        notificationManager.notify(HYDRATION_NOTIFICATION_ID, notification)
        
        // Update last reminder time and log the execution
        val editor = sharedPreferences.edit()
        editor.putLong("last_water_reminder", currentTime)
        editor.putLong("last_water_notification_time", currentTime)
        editor.apply()
    }
    
    fun showMealNotification() {
        val currentTime = System.currentTimeMillis()
        val scheduledTime = sharedPreferences.getLong("meal_reminder_scheduled_time", currentTime)
        val intervalSeconds = sharedPreferences.getInt("meal_interval_seconds", 14400)
        
        val intent = Intent(context, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Create notification with timing info
        val intervalText = sharedPreferences.getString("meal_interval_display", "")
        val notificationText = if (intervalText != null && intervalText.isNotEmpty()) {
            "Your $intervalText reminder is complete! Time for a meal."
        } else {
            "Don't forget to nourish your body with a healthy meal!"
        }
        
        val notification = NotificationCompat.Builder(context, MEAL_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🍽️ Meal Time!")
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()
        
        notificationManager.notify(MEAL_NOTIFICATION_ID, notification)
        
        // Log the execution time
        val editor = sharedPreferences.edit()
        editor.putLong("last_meal_notification_time", currentTime)
        editor.apply()
    }
    
    fun showTestNotification(message: String) {
        val intent = Intent(context, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, HYDRATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("✅ Reminder Set!")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .build()
        
        notificationManager.notify(9999, notification)
    }
}

class HydrationReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val notificationHelper = NotificationManagerHelper(applicationContext)
        notificationHelper.showHydrationNotification()
        
        // For short intervals, reschedule the next reminder
        val sharedPreferences = applicationContext.getSharedPreferences("MyWellnessUser", Context.MODE_PRIVATE)
        val intervalSeconds = sharedPreferences.getInt("water_interval_seconds", 3600)
        val isEnabled = sharedPreferences.getBoolean("water_reminders_enabled", false)
        
        if (isEnabled && intervalSeconds < 900) { // Less than 15 minutes
            val nextWork = OneTimeWorkRequestBuilder<HydrationReminderWorker>()
                .setInitialDelay(intervalSeconds.toLong(), TimeUnit.SECONDS)
                .addTag("WATER_REMINDER")
                .build()
            
            WorkManager.getInstance(applicationContext).enqueueUniqueWork(
                NotificationManagerHelper.HYDRATION_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                nextWork
            )
        }
        
        return Result.success()
    }
}

class MealReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val notificationHelper = NotificationManagerHelper(applicationContext)
        notificationHelper.showMealNotification()
        
        // For short intervals, reschedule the next reminder
        val sharedPreferences = applicationContext.getSharedPreferences("MyWellnessUser", Context.MODE_PRIVATE)
        val intervalSeconds = sharedPreferences.getInt("meal_interval_seconds", 14400)
        val isEnabled = sharedPreferences.getBoolean("meal_reminders_enabled", false)
        
        if (isEnabled && intervalSeconds < 900) { // Less than 15 minutes
            val nextWork = OneTimeWorkRequestBuilder<MealReminderWorker>()
                .setInitialDelay(intervalSeconds.toLong(), TimeUnit.SECONDS)
                .addTag("MEAL_REMINDER")
                .build()
            
            WorkManager.getInstance(applicationContext).enqueueUniqueWork(
                NotificationManagerHelper.MEAL_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                nextWork
            )
        }
        
        return Result.success()
    }
}
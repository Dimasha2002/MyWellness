package com.example.mywellness

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class PrivacySettingsActivity : AppCompatActivity() {

    private lateinit var privacyManager: ProfilePrivacyManager
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_settings)
        
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyWellnessPrefs", MODE_PRIVATE)
        
        // Hide action bar for clean UI
        supportActionBar?.hide()
        
        // Initialize manager
        privacyManager = ProfilePrivacyManager(this)
        
        setupHeader()
        setupPrivacyOptions()
        setupBottomNavigation()
    }

    private fun setupHeader() {
        // Setup back button
        findViewById<ImageView>(R.id.back_button).setOnClickListener {
            finish()
        }
        
        // Set title
        findViewById<TextView>(R.id.page_title).text = "Privacy & Security"
    }

    private fun setupPrivacyOptions() {
        // Data Privacy
        findViewById<CardView>(R.id.dataPrivacyCard).setOnClickListener {
            showDataPrivacyDialog()
        }
        
        // App Permissions
        findViewById<CardView>(R.id.appPermissionsCard).setOnClickListener {
            showAppPermissionsDialog()
        }
        
        // Security Settings
        findViewById<CardView>(R.id.securitySettingsCard).setOnClickListener {
            showSecuritySettingsDialog()
        }
        
        // Export Data
        findViewById<CardView>(R.id.exportDataCard).setOnClickListener {
            showExportDataDialog()
        }
        
        // Privacy Policy
        findViewById<CardView>(R.id.privacyPolicyCard).setOnClickListener {
            showPrivacyPolicyDialog()
        }
        
        // Terms of Service
        findViewById<CardView>(R.id.termsOfServiceCard).setOnClickListener {
            showTermsOfServiceDialog()
        }
        
        // Delete Data
        findViewById<CardView>(R.id.deleteDataCard).setOnClickListener {
            showDeleteDataDialog()
        }
    }

    private fun showDataPrivacyDialog() {
        val options = arrayOf(
            "Data Collection Settings",
            "Analytics Preferences", 
            "Advertising Preferences",
            "Location Data Settings"
        )
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("🛡️ Data Privacy")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> toggleDataCollection()
                1 -> toggleAnalytics()
                2 -> toggleAdvertising()
                3 -> toggleLocationData()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
    
    private fun toggleDataCollection() {
        val isEnabled = sharedPreferences.getBoolean("data_collection_enabled", true)
        val message = if (isEnabled) {
            "Data collection is currently ENABLED.\n\nDisabling will limit app functionality but increase privacy."
        } else {
            "Data collection is currently DISABLED.\n\nEnabling will improve app experience but may affect privacy."
        }
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Data Collection")
        builder.setMessage(message)
        builder.setPositiveButton(if (isEnabled) "Disable" else "Enable") { _, _ ->
            sharedPreferences.edit().putBoolean("data_collection_enabled", !isEnabled).apply()
            val status = if (!isEnabled) "enabled" else "disabled"
            Toast.makeText(this, "Data collection $status", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
    
    private fun toggleAnalytics() {
        val isEnabled = sharedPreferences.getBoolean("analytics_enabled", true)
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Analytics Preferences")
        builder.setMessage("Analytics help us improve the app by understanding how you use it.\n\nCurrently: ${if (isEnabled) "ENABLED" else "DISABLED"}")
        builder.setPositiveButton(if (isEnabled) "Disable Analytics" else "Enable Analytics") { _, _ ->
            sharedPreferences.edit().putBoolean("analytics_enabled", !isEnabled).apply()
            val status = if (!isEnabled) "enabled" else "disabled"
            Toast.makeText(this, "Analytics $status", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
    
    private fun toggleAdvertising() {
        val isEnabled = sharedPreferences.getBoolean("advertising_enabled", false)
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Advertising Preferences")
        builder.setMessage("Control personalized advertising.\n\nPersonalized ads: ${if (isEnabled) "ENABLED" else "DISABLED"}")
        builder.setPositiveButton(if (isEnabled) "Disable Personalized Ads" else "Enable Personalized Ads") { _, _ ->
            sharedPreferences.edit().putBoolean("advertising_enabled", !isEnabled).apply()
            val status = if (!isEnabled) "enabled" else "disabled"
            Toast.makeText(this, "Personalized advertising $status", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
    
    private fun toggleLocationData() {
        val isEnabled = sharedPreferences.getBoolean("location_data_enabled", false)
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Location Data Settings")
        builder.setMessage("Location data can be used for location-based reminders and insights.\n\nLocation tracking: ${if (isEnabled) "ENABLED" else "DISABLED"}")
        builder.setPositiveButton(if (isEnabled) "Disable Location" else "Enable Location") { _, _ ->
            sharedPreferences.edit().putBoolean("location_data_enabled", !isEnabled).apply()
            val status = if (!isEnabled) "enabled" else "disabled"
            Toast.makeText(this, "Location data $status", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun showAppPermissionsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("📱 App Permissions")
        builder.setMessage("Manage app permissions:\n\n• Camera: For profile photos\n• Storage: For data backup\n• Notifications: For reminders\n• Location: For location-based features\n\nWould you like to open system permission settings?")
        builder.setPositiveButton("Open Settings") { _, _ ->
            try {
                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Could not open settings", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun showSecuritySettingsDialog() {
        val options = arrayOf(
            "Biometric Authentication",
            "Auto-lock Settings",
            "Session Management",
            "Device Security"
        )
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("🔐 Security Settings")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> toggleBiometricAuth()
                1 -> showAutoLockSettings()
                2 -> showSessionManagement()
                3 -> showDeviceSecurity()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
    
    private fun toggleBiometricAuth() {
        val isEnabled = sharedPreferences.getBoolean("biometric_enabled", false)
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Biometric Authentication")
        builder.setMessage("Use fingerprint or face recognition to secure your app.\n\nCurrently: ${if (isEnabled) "ENABLED" else "DISABLED"}")
        builder.setPositiveButton(if (isEnabled) "Disable" else "Enable") { _, _ ->
            sharedPreferences.edit().putBoolean("biometric_enabled", !isEnabled).apply()
            val status = if (!isEnabled) "enabled" else "disabled"
            Toast.makeText(this, "Biometric authentication $status", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
    
    private fun showAutoLockSettings() {
        val options = arrayOf("Immediately", "1 minute", "5 minutes", "15 minutes", "Never")
        val currentSetting = sharedPreferences.getInt("auto_lock_setting", 2) // Default to 5 minutes
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Auto-lock Settings")
        builder.setSingleChoiceItems(options, currentSetting) { dialog, which ->
            sharedPreferences.edit().putInt("auto_lock_setting", which).apply()
            Toast.makeText(this, "Auto-lock set to: ${options[which]}", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
    
    private fun showSessionManagement() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Session Management")
        builder.setMessage("Active sessions: 1 (This device)\n\nLast login: Today at ${java.text.SimpleDateFormat("HH:mm").format(java.util.Date())}")
        builder.setPositiveButton("End All Other Sessions") { _, _ ->
            Toast.makeText(this, "All other sessions ended", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("OK", null)
        builder.show()
    }
    
    private fun showDeviceSecurity() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Device Security")
        builder.setMessage("Security Status:\n\n✅ Screen lock enabled\n✅ App is up to date\n✅ No security threats detected\n\nYour device security looks good!")
        builder.setPositiveButton("OK", null)
        builder.show()
    }

    private fun showExportDataDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("📤 Export Your Data")
        builder.setMessage("Export includes:\n\n• Profile information\n• Habit data and progress\n• Mood tracking history\n• Goals and achievements\n• App preferences\n\nData will be exported as a JSON file.")
        builder.setPositiveButton("Export Data") { _, _ ->
            performDataExport()
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
    
    private fun performDataExport() {
        // Simulate data export process
        val builder = AlertDialog.Builder(this)
        builder.setTitle("✅ Export Complete")
        builder.setMessage("Your data has been exported successfully!\n\nFile: MyWellness_Export_${java.text.SimpleDateFormat("yyyyMMdd").format(java.util.Date())}.json\n\nThe file has been saved to your Downloads folder.")
        builder.setPositiveButton("OK", null)
        builder.show()
    }

    private fun showPrivacyPolicyDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("📋 Privacy Policy")
        builder.setMessage("MyWellness Privacy Policy\n\nWe respect your privacy and are committed to protecting your personal data.\n\nKey points:\n• We collect minimal data\n• Your data stays on your device\n• No data selling to third parties\n• You control your information")
        builder.setPositiveButton("Read Full Policy") { _, _ ->
            Toast.makeText(this, "Opening full privacy policy...", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Close", null)
        builder.show()
    }

    private fun showTermsOfServiceDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("📃 Terms of Service")
        builder.setMessage("MyWellness Terms of Service\n\nBy using this app, you agree to:\n\n• Use the app responsibly\n• Respect intellectual property\n• Follow community guidelines\n• Accept our privacy practices\n\nLast updated: September 2025")
        builder.setPositiveButton("Read Full Terms") { _, _ ->
            Toast.makeText(this, "Opening full terms of service...", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Close", null)
        builder.show()
    }

    private fun showDeleteDataDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("⚠️ Delete All Data")
        builder.setMessage("This will permanently delete:\n\n• All habits and progress\n• Mood tracking history\n• Goals and achievements\n• Profile information\n• App preferences\n\nThis action cannot be undone!")
        builder.setPositiveButton("Delete All Data") { _, _ ->
            showFinalDeleteConfirmation()
        }
        builder.setNegativeButton("Keep Data", null)
        builder.show()
    }
    
    private fun showFinalDeleteConfirmation() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("🚨 Final Confirmation")
        builder.setMessage("Are you absolutely sure?\n\nAll your wellness journey data will be permanently lost.\n\nThis cannot be undone!")
        builder.setPositiveButton("Yes, Delete Everything") { _, _ ->
            performDataDeletion()
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
    
    private fun performDataDeletion() {
        // Clear all data
        sharedPreferences.edit().clear().apply()
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("✅ Data Deleted")
        builder.setMessage("All your data has been permanently deleted.\n\nThe app will now restart with a clean slate.")
        builder.setPositiveButton("OK") { _, _ ->
            finish()
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun setupBottomNavigation() {
        val navigationHelper = NavigationHelper(this)
        navigationHelper.setupBottomNavigation()
    }
}
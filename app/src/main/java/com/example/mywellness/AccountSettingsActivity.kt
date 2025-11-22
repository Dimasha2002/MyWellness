package com.example.mywellness

import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class AccountSettingsActivity : AppCompatActivity() {
    
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)
        
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyWellnessPrefs", MODE_PRIVATE)
        
        // Hide action bar for clean UI
        supportActionBar?.hide()
        
        setupHeader()
        setupAccountOptions()
        setupBottomNavigation()
    }

    private fun setupHeader() {
        // Setup back button
        findViewById<ImageView>(R.id.back_button).setOnClickListener {
            finish()
        }
        
        // Set title
        findViewById<TextView>(R.id.page_title).text = "Account Settings"
    }

    private fun setupAccountOptions() {
        // Edit Profile
        findViewById<CardView>(R.id.editProfileCard).setOnClickListener {
            showEditProfileDialog()
        }
        
        // Change Password
        findViewById<CardView>(R.id.changePasswordCard).setOnClickListener {
            showChangePasswordDialog()
        }
        
        // Login & Security
        findViewById<CardView>(R.id.loginSecurityCard).setOnClickListener {
            showLoginSecurityDialog()
        }
        
        // Linked Accounts
        findViewById<CardView>(R.id.linkedAccountsCard).setOnClickListener {
            showLinkedAccountsDialog()
        }
        
        // Delete Account
        findViewById<CardView>(R.id.deleteAccountCard).setOnClickListener {
            showDeleteAccountDialog()
        }
    }

    private fun showEditProfileDialog() {
        val options = arrayOf("Change Display Name", "Update Bio", "Edit Email", "Change Phone Number")
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("✏️ Edit Profile")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> showChangeNameDialog()
                1 -> showChangeBioDialog()
                2 -> showChangeEmailDialog()
                3 -> showChangePhoneDialog()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
    
    private fun showChangeNameDialog() {
        val input = EditText(this)
        input.hint = "Enter new display name"
        val currentName = sharedPreferences.getString("username", "") ?: ""
        input.setText(currentName)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Change Display Name")
        builder.setView(input)
        builder.setPositiveButton("Save") { _, _ ->
            val newName = input.text.toString().trim()
            if (newName.isNotEmpty()) {
                sharedPreferences.edit().putString("username", newName).apply()
                Toast.makeText(this, "Display name updated to: $newName", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
    
    private fun showChangeBioDialog() {
        val input = EditText(this)
        input.hint = "Tell us about yourself..."
        input.maxLines = 3
        val currentBio = sharedPreferences.getString("user_bio", "") ?: ""
        input.setText(currentBio)
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Update Bio")
        builder.setView(input)
        builder.setPositiveButton("Save") { _, _ ->
            val newBio = input.text.toString().trim()
            sharedPreferences.edit().putString("user_bio", newBio).apply()
            Toast.makeText(this, "Bio updated successfully!", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
    
    private fun showChangeEmailDialog() {
        val input = EditText(this)
        input.hint = "Enter new email address"
        input.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        val currentEmail = sharedPreferences.getString("user_email", "") ?: ""
        input.setText(currentEmail)
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Change Email")
        builder.setView(input)
        builder.setPositiveButton("Save") { _, _ ->
            val newEmail = input.text.toString().trim()
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                sharedPreferences.edit().putString("user_email", newEmail).apply()
                Toast.makeText(this, "Email updated to: $newEmail", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
    
    private fun showChangePhoneDialog() {
        val input = EditText(this)
        input.hint = "Enter phone number"
        input.inputType = InputType.TYPE_CLASS_PHONE
        val currentPhone = sharedPreferences.getString("user_phone", "") ?: ""
        input.setText(currentPhone)
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Change Phone Number")
        builder.setView(input)
        builder.setPositiveButton("Save") { _, _ ->
            val newPhone = input.text.toString().trim()
            if (newPhone.isNotEmpty()) {
                sharedPreferences.edit().putString("user_phone", newPhone).apply()
                Toast.makeText(this, "Phone number updated successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Phone number cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun showChangePasswordDialog() {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 20, 50, 20)
        
        val currentPasswordInput = EditText(this)
        currentPasswordInput.hint = "Current Password"
        currentPasswordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        
        val newPasswordInput = EditText(this)
        newPasswordInput.hint = "New Password (min 8 characters)"
        newPasswordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        
        val confirmPasswordInput = EditText(this)
        confirmPasswordInput.hint = "Confirm New Password"
        confirmPasswordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        
        layout.addView(currentPasswordInput)
        layout.addView(newPasswordInput)
        layout.addView(confirmPasswordInput)
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("🔑 Change Password")
        builder.setView(layout)
        builder.setPositiveButton("Change Password") { _, _ ->
            val currentPassword = currentPasswordInput.text.toString()
            val newPassword = newPasswordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()
            
            // Validate current password (check against stored password)
            val storedPassword = sharedPreferences.getString("user_password", "") ?: ""
            
            when {
                currentPassword != storedPassword && storedPassword.isNotEmpty() -> {
                    Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show()
                }
                newPassword.length < 8 -> {
                    Toast.makeText(this, "New password must be at least 8 characters", Toast.LENGTH_SHORT).show()
                }
                newPassword != confirmPassword -> {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    sharedPreferences.edit().putString("user_password", newPassword).apply()
                    Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun showLoginSecurityDialog() {
        val options = arrayOf(
            "Toggle Two-Factor Authentication", 
            "Update Recovery Email", 
            "Set Security Question",
            "View Login History"
        )
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("🛡️ Login & Security")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> toggleTwoFactorAuth()
                1 -> updateRecoveryEmail()
                2 -> setSecurityQuestion()
                3 -> viewLoginHistory()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
    
    private fun toggleTwoFactorAuth() {
        val is2FAEnabled = sharedPreferences.getBoolean("two_factor_enabled", false)
        val message = if (is2FAEnabled) {
            "Two-Factor Authentication is currently ENABLED.\n\nDisabling 2FA will make your account less secure."
        } else {
            "Two-Factor Authentication is currently DISABLED.\n\nEnabling 2FA will add an extra layer of security to your account."
        }
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Two-Factor Authentication")
        builder.setMessage(message)
        builder.setPositiveButton(if (is2FAEnabled) "Disable" else "Enable") { _, _ ->
            sharedPreferences.edit().putBoolean("two_factor_enabled", !is2FAEnabled).apply()
            val status = if (!is2FAEnabled) "enabled" else "disabled"
            Toast.makeText(this, "Two-Factor Authentication $status", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
    
    private fun updateRecoveryEmail() {
        val input = EditText(this)
        input.hint = "Enter recovery email address"
        input.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        val currentRecoveryEmail = sharedPreferences.getString("recovery_email", "") ?: ""
        input.setText(currentRecoveryEmail)
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Update Recovery Email")
        builder.setMessage("This email will be used for account recovery and security alerts.")
        builder.setView(input)
        builder.setPositiveButton("Save") { _, _ ->
            val newEmail = input.text.toString().trim()
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                sharedPreferences.edit().putString("recovery_email", newEmail).apply()
                Toast.makeText(this, "Recovery email updated successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
    
    private fun setSecurityQuestion() {
        val questions = arrayOf(
            "What was your first pet's name?",
            "What city were you born in?",
            "What was your mother's maiden name?",
            "What was your first car?",
            "What elementary school did you attend?"
        )
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose Security Question")
        builder.setItems(questions) { _, which ->
            showSecurityAnswerDialog(questions[which])
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
    
    private fun showSecurityAnswerDialog(question: String) {
        val input = EditText(this)
        input.hint = "Enter your answer"
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Security Answer")
        builder.setMessage("Question: $question")
        builder.setView(input)
        builder.setPositiveButton("Save") { _, _ ->
            val answer = input.text.toString().trim()
            if (answer.isNotEmpty()) {
                sharedPreferences.edit()
                    .putString("security_question", question)
                    .putString("security_answer", answer.lowercase())
                    .apply()
                Toast.makeText(this, "Security question set successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Answer cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
    
    private fun viewLoginHistory() {
        val loginHistory = sharedPreferences.getString("login_history", "No recent logins recorded") ?: "No recent logins recorded"
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Recent Login Activity")
        builder.setMessage("Recent logins:\n\n$loginHistory\n\nIf you notice any suspicious activity, please change your password immediately.")
        builder.setPositiveButton("OK", null)
        builder.setNeutralButton("Clear History") { _, _ ->
            sharedPreferences.edit().remove("login_history").apply()
            Toast.makeText(this, "Login history cleared", Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }

    private fun showLinkedAccountsDialog() {
        val accounts = arrayOf("Google Account", "Facebook Account", "Apple ID", "Twitter Account")
        val checkedItems = booleanArrayOf(
            sharedPreferences.getBoolean("google_linked", false),
            sharedPreferences.getBoolean("facebook_linked", false),
            sharedPreferences.getBoolean("apple_linked", false),
            sharedPreferences.getBoolean("twitter_linked", false)
        )
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("🔗 Linked Accounts")
        builder.setMultiChoiceItems(accounts, checkedItems) { _, which, isChecked ->
            checkedItems[which] = isChecked
        }
        builder.setPositiveButton("Save Changes") { _, _ ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("google_linked", checkedItems[0])
            editor.putBoolean("facebook_linked", checkedItems[1])
            editor.putBoolean("apple_linked", checkedItems[2])
            editor.putBoolean("twitter_linked", checkedItems[3])
            editor.apply()
            
            val linkedCount = checkedItems.count { it }
            Toast.makeText(this, "$linkedCount accounts linked successfully!", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Cancel", null)
        builder.setNeutralButton("Unlink All") { _, _ ->
            showUnlinkAllAccountsDialog()
        }
        builder.show()
    }
    
    private fun showUnlinkAllAccountsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("⚠️ Unlink All Accounts")
        builder.setMessage("Are you sure you want to unlink all connected accounts?\n\nThis will:\n• Remove all social login options\n• Require password login only\n• Stop data synchronization")
        builder.setPositiveButton("Unlink All") { _, _ ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("google_linked", false)
            editor.putBoolean("facebook_linked", false)
            editor.putBoolean("apple_linked", false)
            editor.putBoolean("twitter_linked", false)
            editor.apply()
            Toast.makeText(this, "All accounts unlinked successfully", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun showDeleteAccountDialog() {
        val options = arrayOf("Export My Data First", "Delete Account Immediately")
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("⚠️ Delete Account")
        builder.setMessage("What would you like to do?")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> exportDataBeforeDelete()
                1 -> showFinalDeleteConfirmation()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
    
    private fun exportDataBeforeDelete() {
        val userData = StringBuilder()
        userData.append("=== MyWellness Account Data Export ===\n\n")
        userData.append("User Name: ${sharedPreferences.getString("user_name", "Not set")}\n")
        userData.append("Email: ${sharedPreferences.getString("user_email", "Not set")}\n")
        userData.append("Bio: ${sharedPreferences.getString("user_bio", "Not set")}\n")
        userData.append("Phone: ${sharedPreferences.getString("user_phone", "Not set")}\n")
        userData.append("2FA Enabled: ${sharedPreferences.getBoolean("two_factor_enabled", false)}\n")
        userData.append("Recovery Email: ${sharedPreferences.getString("recovery_email", "Not set")}\n")
        userData.append("\nLinked Accounts:\n")
        userData.append("- Google: ${if (sharedPreferences.getBoolean("google_linked", false)) "Linked" else "Not linked"}\n")
        userData.append("- Facebook: ${if (sharedPreferences.getBoolean("facebook_linked", false)) "Linked" else "Not linked"}\n")
        userData.append("- Apple: ${if (sharedPreferences.getBoolean("apple_linked", false)) "Linked" else "Not linked"}\n")
        userData.append("- Twitter: ${if (sharedPreferences.getBoolean("twitter_linked", false)) "Linked" else "Not linked"}\n")
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("📄 Your Data Export")
        builder.setMessage(userData.toString())
        builder.setPositiveButton("Save & Continue to Delete") { _, _ ->
            Toast.makeText(this, "Data exported successfully!", Toast.LENGTH_SHORT).show()
            showFinalDeleteConfirmation()
        }
        builder.setNegativeButton("Cancel Delete", null)
        builder.show()
    }

    private fun showFinalDeleteConfirmation() {
        val input = EditText(this)
        input.hint = "Type 'DELETE' to confirm"
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("🚨 Final Confirmation")
        builder.setMessage("This action cannot be undone!\n\nAll your wellness journey data will be permanently lost.\n\nType 'DELETE' to confirm:")
        builder.setView(input)
        builder.setPositiveButton("Delete Forever") { _, _ ->
            val confirmation = input.text.toString().trim()
            if (confirmation.equals("DELETE", ignoreCase = true)) {
                performAccountDeletion()
            } else {
                Toast.makeText(this, "Confirmation text doesn't match. Account not deleted.", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
    
    private fun performAccountDeletion() {
        // Clear all user data
        sharedPreferences.edit().clear().apply()
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("✅ Account Deleted")
        builder.setMessage("Your account has been successfully deleted.\n\nAll personal data has been removed from the device.\n\nThank you for using MyWellness!")
        builder.setPositiveButton("OK") { _, _ ->
            // In a real app, you would navigate to login screen or close the app
            Toast.makeText(this, "Goodbye! 👋", Toast.LENGTH_SHORT).show()
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
package com.example.mywellness

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import android.content.SharedPreferences
import android.text.InputType

class SignInSignUpActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_sign_up)
        sharedPreferences = getSharedPreferences("MyWellnessUser", MODE_PRIVATE)

        findViewById<Button>(R.id.btnSignIn).setOnClickListener {
            showLoginDialog()
        }
        findViewById<Button>(R.id.btnSignUp).setOnClickListener {
            showSignUpDialog()
        }
    }

    private fun showSignUpDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_signup, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val nameEdit = dialogView.findViewById<EditText>(R.id.editName)
        val emailEdit = dialogView.findViewById<EditText>(R.id.editEmail)
        val passwordEdit = dialogView.findViewById<EditText>(R.id.editPassword)
        val confirmEdit = dialogView.findViewById<EditText>(R.id.editConfirmPassword)
        val signUpBtn = dialogView.findViewById<Button>(R.id.btnDialogSignUp)

        signUpBtn.setOnClickListener {
            val name = nameEdit.text.toString().trim()
            val email = emailEdit.text.toString().trim()
            val password = passwordEdit.text.toString()
            val confirm = confirmEdit.text.toString()
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != confirm) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Save credentials
            val editor = sharedPreferences.edit()
            editor.putString("username", name)
            editor.putString("password", password)
            editor.apply()
            Toast.makeText(this, "Account created! Please log in.", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            showLoginDialog()
        }
        dialog.show()
    }

    private fun showLoginDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_login, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val usernameEdit = dialogView.findViewById<EditText>(R.id.editLoginUsername)
        val passwordEdit = dialogView.findViewById<EditText>(R.id.editLoginPassword)
        val togglePasswordVisibility = dialogView.findViewById<ImageView>(R.id.togglePasswordVisibility)
        val loginBtn = dialogView.findViewById<Button>(R.id.btnDialogLogin)

        // Password visibility toggle functionality
        var isPasswordVisible = false
        togglePasswordVisibility.setOnClickListener {
            if (isPasswordVisible) {
                // Hide password
                passwordEdit.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                togglePasswordVisibility.setImageResource(android.R.drawable.ic_partial_secure)
                isPasswordVisible = false
            } else {
                // Show password
                passwordEdit.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                togglePasswordVisibility.setImageResource(android.R.drawable.ic_menu_view)
                isPasswordVisible = true
            }
            // Move cursor to end of text
            passwordEdit.setSelection(passwordEdit.text.length)
        }

        loginBtn.setOnClickListener {
            val username = usernameEdit.text.toString().trim()
            val password = passwordEdit.text.toString()
            val savedUsername = sharedPreferences.getString("username", "")
            val savedPassword = sharedPreferences.getString("password", "")
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (username == savedUsername && password == savedPassword) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                goToHome()
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    private fun goToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}

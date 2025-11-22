package com.example.mywellness

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Onboarding3Activity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.mywellness.R.layout.onboarding_screen3)
        
        setupGetStartedButton()
    }
    
    private fun setupGetStartedButton() {
        val btnGetStarted = findViewById<Button>(com.example.mywellness.R.id.btnGetStarted)
        btnGetStarted.setOnClickListener {
            val intent = Intent(this, SignInSignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
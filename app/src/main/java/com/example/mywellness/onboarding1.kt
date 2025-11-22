package com.example.mywellness

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Onboarding1Activity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.mywellness.R.layout.onboarding_screen1)
        
        setupNextButton()
    }
    
    private fun setupNextButton() {
        val btnNext = findViewById<Button>(com.example.mywellness.R.id.btnNext)
        btnNext.setOnClickListener {
            val intent = Intent(this, Onboarding2Activity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
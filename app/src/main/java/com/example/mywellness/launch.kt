package com.example.mywellness

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LaunchActivity : AppCompatActivity() {
    
    companion object {
        private const val SPLASH_DELAY = 3000L // 3 seconds
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.mywellness.R.layout.launch_screen)
        
        // Hide action bar for full screen splash
        supportActionBar?.hide()
        
        startSplashTimer()
    }
    
    private fun startSplashTimer() {
        lifecycleScope.launch {
            delay(SPLASH_DELAY)
            if (!isFinishing) {
                navigateToOnboarding()
            }
        }
    }
    
    private fun navigateToOnboarding() {
        val intent = Intent(this, Onboarding1Activity::class.java)
        startActivity(intent)
        finish() // Prevent going back to splash screen
        
        // Add smooth transition animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
package com.example.mywellness

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class AboutActivity : AppCompatActivity() {

    private lateinit var aboutManager: ProfileAboutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        
        // Hide action bar for clean UI
        supportActionBar?.hide()
        
        // Initialize manager
        aboutManager = ProfileAboutManager(this)
        
        setupHeader()
        setupAboutOptions()
        loadAppInfo()
        setupBottomNavigation()
    }

    private fun setupHeader() {
        // Setup back button
        findViewById<ImageView>(R.id.back_button).setOnClickListener {
            finish()
        }
        
        // Set title
        findViewById<TextView>(R.id.page_title).text = "About MyWellness"
    }

    private fun setupAboutOptions() {
        // Help & Support
        findViewById<CardView>(R.id.helpSupportCard).setOnClickListener {
            aboutManager.openHelpSupport()
        }
        
        // Privacy Policy
        findViewById<CardView>(R.id.privacyPolicyCard).setOnClickListener {
            aboutManager.openPrivacyPolicy()
        }
        
        // Terms of Service
        findViewById<CardView>(R.id.termsOfServiceCard).setOnClickListener {
            aboutManager.openTermsOfService()
        }
        
        // Share App
        findViewById<CardView>(R.id.shareAppCard).setOnClickListener {
            aboutManager.shareApp()
        }
        
        // Rate App
        findViewById<CardView>(R.id.rateAppCard).setOnClickListener {
            aboutManager.rateApp()
        }
    }

    private fun loadAppInfo() {
        // Load app version and build info
        findViewById<TextView>(R.id.appVersionText).text = "Version ${aboutManager.getAppVersion()}"
        findViewById<TextView>(R.id.buildNumberText).text = "Build ${aboutManager.getBuildNumber()}"
    }

    private fun setupBottomNavigation() {
        val navigationHelper = NavigationHelper(this)
        navigationHelper.setupBottomNavigation()
    }
}
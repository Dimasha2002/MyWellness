package com.example.mywellness

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.cardview.widget.CardView

class ProfileAboutManager(private val context: Context) {

    fun setupAboutSettings(aboutCard: CardView) {
        aboutCard.setOnClickListener {
            // Navigate to about page
            val intent = Intent(context, AboutActivity::class.java)
            context.startActivity(intent)
        }
    }

    fun openHelpSupport() {
        // Open help and support section
        // Could open a web page or in-app help
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://mywellness.app/help"))
        context.startActivity(intent)
    }

    fun openTermsOfService() {
        // Open terms of service
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://mywellness.app/terms"))
        context.startActivity(intent)
    }

    fun openPrivacyPolicy() {
        // Open privacy policy
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://mywellness.app/privacy"))
        context.startActivity(intent)
    }

    fun shareApp() {
        // Share the app with others
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Check out MyWellness - the best app for tracking your wellness journey!")
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share MyWellness"))
    }

    fun rateApp() {
        // Open app store for rating
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.example.mywellness"))
        context.startActivity(intent)
    }

    fun getAppVersion(): String {
        return "1.0.0"
    }

    fun getBuildNumber(): String {
        return "100"
    }
}
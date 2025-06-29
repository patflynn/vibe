package dev.broken.app.vibe

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.content.ContextCompat

/**
 * Manages feedback collection functionality including Play Store reviews,
 * GitHub issue reporting, and email feedback.
 */
class FeedbackManager(private val context: Context) {
    
    companion object {
        private const val PLAY_STORE_PACKAGE = "com.android.vending"
        private const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id="
        private const val GITHUB_ISSUES_URL = "https://github.com/patflynn/vibe/issues/new"
        private const val GITHUB_REPO_URL = "https://github.com/patflynn/vibe"
        private const val FEEDBACK_EMAIL = "feedback@broken.dev"
    }
    
    /**
     * Opens the Play Store to allow users to rate and review the app
     */
    fun openPlayStoreReview() {
        val packageName = context.packageName
        
        try {
            // Try to open in Play Store app first
            val playStoreIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=$packageName")
                setPackage(PLAY_STORE_PACKAGE)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            if (isIntentAvailable(playStoreIntent)) {
                context.startActivity(playStoreIntent)
            } else {
                // Fallback to web browser
                openPlayStoreInBrowser(packageName)
            }
        } catch (e: ActivityNotFoundException) {
            openPlayStoreInBrowser(packageName)
        }
    }
    
    private fun openPlayStoreInBrowser(packageName: String) {
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("$PLAY_STORE_URL$packageName")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(browserIntent)
        } catch (e: ActivityNotFoundException) {
            showToast(context.getString(R.string.no_browser_app))
        }
    }
    
    /**
     * Opens GitHub issues page for bug reports and feature requests
     */
    fun openGitHubIssues() {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(GITHUB_ISSUES_URL)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            showToast(context.getString(R.string.no_browser_app))
        }
    }
    
    /**
     * Opens GitHub repository page
     */
    fun openGitHubRepository() {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(GITHUB_REPO_URL)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            showToast(context.getString(R.string.no_browser_app))
        }
    }
    
    /**
     * Opens email client to send feedback with pre-filled content
     */
    fun sendFeedbackEmail() {
        val appVersion = getAppVersion()
        val deviceInfo = getDeviceInfo()
        val androidVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
        
        val emailBody = context.getString(
            R.string.feedback_email_body,
            appVersion,
            deviceInfo,
            androidVersion
        )
        
        try {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(FEEDBACK_EMAIL))
                putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.feedback_email_subject))
                putExtra(Intent.EXTRA_TEXT, emailBody)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            if (isIntentAvailable(emailIntent)) {
                val chooser = Intent.createChooser(
                    emailIntent,
                    context.getString(R.string.email_chooser_title)
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(chooser)
            } else {
                showToast(context.getString(R.string.no_email_app))
            }
        } catch (e: ActivityNotFoundException) {
            showToast(context.getString(R.string.no_email_app))
        }
    }
    
    /**
     * Gets the app version name and code
     */
    fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "${packageInfo.versionName} (${packageInfo.longVersionCode})"
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown"
        }
    }
    
    private fun getDeviceInfo(): String {
        return "${Build.MANUFACTURER} ${Build.MODEL}"
    }
    
    private fun isIntentAvailable(intent: Intent): Boolean {
        return context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isNotEmpty()
    }
    
    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}
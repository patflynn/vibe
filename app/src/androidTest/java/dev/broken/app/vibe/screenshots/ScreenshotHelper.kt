package dev.broken.app.vibe.screenshots

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnitRunner
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * This class is an extension of the test runner for the screenshot tests
 * but we're not actually using it as the main runner to avoid CI issues.
 * It serves as a helper class for screenshot organization.
 */
class ScreenshotHelper {

    companion object {
        private const val TAG = "ScreenshotHelper"
        private const val SCREENSHOTS_ARG = "captureScreenshots"
        
        /**
         * Initialize screenshot directories and settings
         */
        fun initialize() {
            // Get timestamp for organizing output
            val timestamp = getTimestampString()
            Log.d(TAG, "Initializing ScreenshotHelper with timestamp=$timestamp")
            
            // Create output directories
            createOutputDirectories()
        }
        
        /**
         * Collect and process screenshot assets after tests
         */
        fun collectAssets() {
            Log.i(TAG, "Collecting screenshot assets")
            
            try {
                processScreenshotAssets()
            } catch (e: Exception) {
                Log.e(TAG, "Error collecting screenshot assets: ${e.message}")
            }
        }

    /**
     * Create output directories for screenshots and recordings
     */
    private fun createOutputDirectories() {
        try {
            val context = InstrumentationRegistry.getInstrumentation().context
            val screenshotDir = File(context.getExternalFilesDir(null), "screenshots")
            val recordingsDir = File(context.getExternalFilesDir(null), "recordings")
            
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs()
            }
            
            if (!recordingsDir.exists()) {
                recordingsDir.mkdirs()
            }
            
            Log.d(TAG, "Created output directories: screenshots=${screenshotDir.absolutePath}, recordings=${recordingsDir.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Error creating output directories: ${e.message}")
        }
    }

    /**
     * Process all screenshot assets after tests complete
     */
    private fun processScreenshotAssets() {
        Log.d(TAG, "Processing screenshot assets")
        
        // Assets will be collected automatically by the CI process from their
        // output locations, so this is just a placeholder for any additional
        // processing that might be needed
    }

    /**
     * Generate a timestamp string for the current test run
     */
    @SuppressLint("SimpleDateFormat")
    private fun getTimestampString(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss")
        return dateFormat.format(Date())
    }
}
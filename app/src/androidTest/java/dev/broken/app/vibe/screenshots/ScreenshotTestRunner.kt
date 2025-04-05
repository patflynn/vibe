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
 * Custom test runner for capturing screenshots and recordings
 * Used to generate assets for the README and documentation
 */
class ScreenshotTestRunner : AndroidJUnitRunner() {

    companion object {
        private const val TAG = "ScreenshotTestRunner"
        private const val SCREENSHOTS_ARG = "captureScreenshots"
    }

    override fun onCreate(arguments: Bundle) {
        val newArgs = Bundle(arguments)
        val captureScreenshots = arguments.getString(SCREENSHOTS_ARG, "true")

        // Set test-specific flags if needed
        Log.d(TAG, "Creating ScreenshotTestRunner with captureScreenshots=$captureScreenshots")

        // Make test output more organized by using a timestamp for this test run
        val timestamp = getTimestampString()
        newArgs.putString("timestamp", timestamp)
        
        // Create directories for screenshots/recordings if they don't exist
        createOutputDirectories()
        
        super.onCreate(newArgs)
    }

    override fun finish(resultCode: Int, results: Bundle) {
        Log.i(TAG, "ScreenshotTestRunner finished with resultCode=$resultCode")
        
        // Process and move files to the correct location if needed
        try {
            collectScreenshotAssets()
        } catch (e: Exception) {
            Log.e(TAG, "Error collecting screenshot assets: ${e.message}")
        }
        
        super.finish(resultCode, results)
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
     * Collect and organize all screenshot assets after tests complete
     */
    private fun collectScreenshotAssets() {
        Log.d(TAG, "Collecting screenshot assets")
        
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
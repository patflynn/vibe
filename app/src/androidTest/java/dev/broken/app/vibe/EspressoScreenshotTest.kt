package dev.broken.app.vibe

import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.screenshot.captureToBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream

/**
 * Test for capturing screenshots using Espresso's native screenshot API.
 * These screenshots will be automatically collected by Firebase Test Lab.
 */
@RunWith(AndroidJUnit4::class)
class EspressoScreenshotTest {

    private lateinit var activityScenario: ActivityScenario<MainActivity>
    private val TAG = "ScreenshotTest"
    
    @Before
    fun setup() {
        // Configure test environment for predictable screenshots
        TestHelpers.configureForTesting()
        
        // Launch the main activity
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
        
        // Wait for UI to stabilize
        TestHelpers.waitFor(1000)
    }
    
    @After
    fun tearDown() {
        // Reset test configuration
        TestHelpers.resetTestConfiguration()
        
        // Close the activity
        activityScenario.close()
    }
    
    /**
     * Save screenshot to both Firebase Test Lab and local storage
     * Firebase Test Lab automatically collects screenshots via Espresso's captureToBitmap
     * We also save locally to ensure we have a backup
     */
    private fun captureScreenshot(viewId: Int, fileName: String) {
        // Capture screenshot as bitmap
        val bitmap = onView(withId(viewId)).captureToBitmap()
        
        // Try to save locally (as a backup)
        try {
            // Get screenshots directory
            val screenshotsDir = File(
                InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "screenshots"
            )
            
            // Create directory if it doesn't exist
            if (!screenshotsDir.exists()) {
                screenshotsDir.mkdirs()
            }
            
            // Save bitmap to file
            val screenshotFile = File(screenshotsDir, fileName)
            FileOutputStream(screenshotFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            
            Log.d(TAG, "Screenshot saved to ${screenshotFile.absolutePath}")
        } catch (e: Exception) {
            // Log error but don't fail test - Firebase Test Lab will still capture it
            Log.e(TAG, "Failed to save screenshot locally", e)
        }
    }
    
    @Test
    fun captureMainScreenDefault() {
        // Take screenshot of the default timer screen
        // Use the root view to capture the entire screen
        captureScreenshot(android.R.id.content, "main_screen_default.png")
        Log.d(TAG, "Default screen captured")
    }
    
    @Test
    fun captureMainScreenTimerRunning() {
        // Start the timer
        onView(withId(R.id.startButton)).perform(click())
        
        // Wait for the UI to update
        TestHelpers.waitFor(500)
        
        // Take screenshot with timer running
        captureScreenshot(android.R.id.content, "main_screen_timer_running.png")
        Log.d(TAG, "Timer running screen captured")
    }
    
    @Test
    fun captureDifferentTimerDurations() {
        // Adjust timer to 20 minutes by swiping right on the slider
        onView(withId(R.id.durationSlider)).perform(swipeRight())
        
        // Wait for the UI to update
        TestHelpers.waitFor(500)
        
        // Take screenshot
        captureScreenshot(android.R.id.content, "main_screen_20min.png")
        Log.d(TAG, "20 minute timer screen captured")
    }
    
    @Test
    fun captureAppDemonstration() {
        // This test demonstrates app functionality
        // Firebase Test Lab will automatically record video
        
        // Adjust slider to show different time values
        onView(withId(R.id.durationSlider)).perform(swipeRight())
        TestHelpers.waitFor(1000)
        
        // Start the timer
        onView(withId(R.id.startButton)).perform(click())
        TestHelpers.waitFor(2000)
        
        // Pause the timer
        onView(withId(R.id.startButton)).perform(click())
        TestHelpers.waitFor(1000)
        
        // Adjust slider again
        onView(withId(R.id.durationSlider)).perform(swipeRight())
        TestHelpers.waitFor(1000)
        
        // Start timer again
        onView(withId(R.id.startButton)).perform(click())
        TestHelpers.waitFor(2000)
        
        // Pause again
        onView(withId(R.id.startButton)).perform(click())
        
        Log.d(TAG, "App demonstration completed")
    }
}
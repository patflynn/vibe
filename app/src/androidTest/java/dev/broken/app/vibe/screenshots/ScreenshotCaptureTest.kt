package dev.broken.app.vibe.screenshots

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.screenshot.Screenshot
import dev.broken.app.vibe.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Test class responsible for generating screenshots and demo assets for the README.
 * These tests don't validate functionality but rather capture visual assets of the app.
 */
@RunWith(AndroidJUnit4::class)
class ScreenshotCaptureTest {

    private lateinit var activityScenario: ActivityScenario<MainActivity>
    private lateinit var context: Context
    private val screenshotDir = "screenshots"
    private val TAG = "ScreenshotTest"

    @Before
    fun setup() {
        // Get the context
        context = InstrumentationRegistry.getInstrumentation().targetContext
        
        // Configure test environment for predictable screenshots
        TestHelpers.configureForTesting()
        
        // Initialize screenshot helper
        ScreenshotHelper.initialize()
        
        // Create screenshot directory if it doesn't exist
        createScreenshotDirectory()
        
        // Launch the main activity
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
    }
    
    @After
    fun tearDown() {
        // Reset test configuration
        TestHelpers.resetTestConfiguration()
        
        // Close the activity
        activityScenario.close()
    }
    
    @Test
    fun captureMainScreenDefault() {
        // Wait for the UI to stabilize
        TestHelpers.waitFor(1000)
        
        // Take screenshot of the default timer screen
        takeScreenshot("main_screen_default")
    }
    
    @Test
    fun captureMainScreenTimerRunning() {
        // Wait for the UI to stabilize
        TestHelpers.waitFor(1000)
        
        // Start the timer
        onView(withId(R.id.startButton)).perform(click())
        
        // Wait for the UI to update
        TestHelpers.waitFor(500)
        
        // Take screenshot with timer running
        takeScreenshot("main_screen_timer_running")
    }
    
    @Test
    fun captureDifferentTimerDurations() {
        // Wait for the UI to stabilize
        TestHelpers.waitFor(1000)
        
        // Adjust timer to 20 minutes by swiping right on the slider
        onView(withId(R.id.durationSlider)).perform(swipeRight())
        
        // Wait for the UI to update
        TestHelpers.waitFor(500)
        
        // Take screenshot with different timer duration
        takeScreenshot("main_screen_20min")
    }
    
    /**
     * Create a directory for storing screenshots if it doesn't exist
     */
    private fun createScreenshotDirectory() {
        try {
            // Get the external storage directory for pictures
            val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val screenshotDirectory = File(storageDir, screenshotDir)
            
            if (!screenshotDirectory.exists()) {
                val success = screenshotDirectory.mkdirs()
                if (success) {
                    Log.d(TAG, "Screenshot directory created successfully")
                } else {
                    Log.e(TAG, "Failed to create screenshot directory")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating screenshot directory: ${e.message}")
        }
    }
    
    /**
     * Take a screenshot and save it with the given filename
     */
    private fun takeScreenshot(filename: String) {
        try {
            // Capture the screenshot
            val screenshot = Screenshot.capture()
            val bitmap = screenshot.bitmap
            
            // Get the file to save to
            val screenshotFile = getScreenshotFile(filename)
            
            // Save the bitmap to the file
            saveBitmap(bitmap, screenshotFile)
            
            Log.d(TAG, "Screenshot saved to $screenshotFile")
        } catch (e: Exception) {
            Log.e(TAG, "Error taking screenshot: ${e.message}")
        }
    }
    
    /**
     * Get a file reference for saving the screenshot
     */
    private fun getScreenshotFile(filename: String): File {
        // Get the external storage directory for pictures
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val screenshotDirectory = File(storageDir, screenshotDir)
        
        // Ensure the directory exists
        if (!screenshotDirectory.exists()) {
            screenshotDirectory.mkdirs()
        }
        
        // Create a timestamp for unique filenames
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        
        // Return the file reference
        return File(screenshotDirectory, "${filename}_${timeStamp}.png")
    }
    
    /**
     * Save a bitmap to a file
     */
    private fun saveBitmap(bitmap: Bitmap, file: File) {
        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error saving bitmap: ${e.message}")
        }
    }
}
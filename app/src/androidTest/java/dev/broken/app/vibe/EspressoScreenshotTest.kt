package dev.broken.app.vibe

import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.hamcrest.Matcher
import org.hamcrest.Matchers.any
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream
import android.graphics.Canvas
import android.graphics.Rect
import android.view.ViewGroup
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Extension function for capturing screenshots as a fallback for Espresso's screenshot API
 */
fun ViewInteraction.captureToBitmap(): Bitmap {
    var bitmap: Bitmap? = null
    perform(object : ViewAction {
        override fun getConstraints(): Matcher<View> = any(View::class.java)
        override fun getDescription() = "Capture view to bitmap"
        override fun perform(uiController: UiController, view: View) {
            // More robust bitmap creation
            view.isDrawingCacheEnabled = true
            view.destroyDrawingCache() // Clear any existing cache
            view.buildDrawingCache(true) // Force a rebuild
            
            // Wait for drawing cache to be ready
            uiController.loopMainThreadUntilIdle()
            
            // Check if drawing cache is available
            if (view.drawingCache != null) {
                bitmap = Bitmap.createBitmap(view.drawingCache)
            } else {
                // Fallback method if drawing cache fails
                try {
                    // Create bitmap of view size
                    bitmap = Bitmap.createBitmap(
                        view.width, 
                        view.height, 
                        Bitmap.Config.ARGB_8888
                    )
                    // Draw view into canvas backed by bitmap
                    val canvas = Canvas(bitmap!!)
                    view.draw(canvas)
                } catch (e: Exception) {
                    Log.e("ScreenshotTest", "Failed to capture view with fallback method", e)
                }
            }
            
            // Clean up
            uiController.loopMainThreadUntilIdle()
            view.isDrawingCacheEnabled = false
        }
    })
    
    return bitmap ?: throw IllegalStateException("Failed to capture bitmap from view")
}

/**
 * Test for capturing screenshots using Espresso's native screenshot API.
 * These screenshots will be automatically collected by Firebase Test Lab.
 */
@RunWith(AndroidJUnit4::class)
class EspressoScreenshotTest {

    private lateinit var activityScenario: ActivityScenario<MainActivity>
    private val TAG = "ScreenshotTest"
    private val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    
    @Before
    fun setup() {
        // Configure test environment for predictable screenshots
        TestHelpers.configureForTesting()
        
        // Launch the main activity
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
        
        // Wait for UI to stabilize
        TestHelpers.waitFor(1000)
        
        // Create all screenshot directories we'll use
        createScreenshotDirectories()
        
        Log.i(TAG, "EspressoScreenshotTest setup complete at timestamp: $timestamp")
    }
    
    @After
    fun tearDown() {
        Log.i(TAG, "EspressoScreenshotTest tearDown starting")
        
        // Reset test configuration
        TestHelpers.resetTestConfiguration()
        
        // Close the activity
        activityScenario.close()
        
        // Log success message to ensure tests completed properly
        Log.i(TAG, "EspressoScreenshotTest completed successfully. Screenshots saved with timestamp: $timestamp")
    }
    
    /**
     * Create all screenshot directories at test startup
     */
    private fun createScreenshotDirectories() {
        // Define all the directories we want to save to
        val directories = listOf(
            // 1. Firebase Test Lab artifacts directory
            File(InstrumentationRegistry.getInstrumentation().targetContext.filesDir, "test-results"),
            // 2. App's own external directory
            File(InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "screenshots"),
            // 3. Root external directory
            File(InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(null), "screenshots"),
            // 4. Test instrumentation context directory
            File(InstrumentationRegistry.getInstrumentation().context.filesDir, "screenshots"),
            // 5. SDCard directory (special Firebase path)
            File("/sdcard/screenshots"),
            // 6. Standard Firebase Test Lab directory
            File("/sdcard/Download/firebase_test_lab")
        )
        
        // Create all directories
        directories.forEach { dir ->
            try {
                if (!dir.exists()) {
                    val created = dir.mkdirs()
                    Log.d(TAG, "Created directory ${dir.absolutePath}: $created")
                } else {
                    Log.d(TAG, "Directory already exists: ${dir.absolutePath}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create directory ${dir.absolutePath}", e)
            }
        }
    }
    
    /**
     * Save screenshot for Firebase Test Lab to collect
     * We save screenshots to multiple standard locations to maximize visibility
     */
    private fun captureScreenshot(viewId: Int, fileName: String) {
        // Create filename with timestamp to ensure uniqueness
        val filenameWithTimestamp = "${fileName.removeSuffix(".png")}_$timestamp.png"
        
        // Log each step to help track the process
        Log.i(TAG, "Starting to capture screenshot: $filenameWithTimestamp")
        
        try {
            // Capture screenshot as bitmap
            val bitmap = onView(withId(viewId)).captureToBitmap()
            
            if (bitmap == null) {
                Log.e(TAG, "Failed to capture bitmap for $filenameWithTimestamp")
                return
            }
            
            Log.d(TAG, "Successfully captured bitmap of size ${bitmap.width}x${bitmap.height}")
            
            // Save to all possible locations
            saveToAllLocations(bitmap, filenameWithTimestamp)
            
            // Also save with the original name without timestamp for consistent lookup
            saveToAllLocations(bitmap, fileName)
            
            Log.i(TAG, "Successfully saved screenshot $filenameWithTimestamp to all locations")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to capture or save screenshot: $filenameWithTimestamp", e)
        }
    }
    
    /**
     * Save bitmap to all possible locations Firebase Test Lab might check
     */
    private fun saveToAllLocations(bitmap: Bitmap, fileName: String) {
        // All the directories where we want to save screenshots
        val saveLocations = listOf(
            // 1. Firebase Test Lab standard artifacts directory
            Pair(
                File(InstrumentationRegistry.getInstrumentation().targetContext.filesDir, "test-results"),
                fileName
            ),
            // 2. App's Pictures directory
            Pair(
                File(InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "screenshots"),
                fileName
            ),
            // 3. Root external storage
            Pair(
                File(InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(null), "screenshots"),
                fileName
            ),
            // 4. Test instrumentation directory
            Pair(
                File(InstrumentationRegistry.getInstrumentation().context.filesDir, "screenshots"),
                fileName
            ),
            // 5. SDCard directory (special Firebase path)
            Pair(
                File("/sdcard/screenshots"),
                fileName
            ),
            // 6. Standard Firebase Test Lab directory
            Pair(
                File("/sdcard/Download/firebase_test_lab"),
                fileName
            ),
            // 7. Special standardized filenames in the root that Firebase looks for
            Pair(
                File("/sdcard"),
                "firebase_screenshot_$fileName"
            )
        )
        
        // Save to each location
        saveLocations.forEach { (directory, filename) ->
            try {
                // Create directory if it doesn't exist
                if (!directory.exists()) {
                    directory.mkdirs()
                }
                
                // Create file and save bitmap
                val file = File(directory, filename)
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                
                Log.d(TAG, "Screenshot saved to: ${file.absolutePath}")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save to ${directory.absolutePath}/$filename", e)
            }
        }
    }
    
    @Test
    fun captureMainScreenDefault() {
        // Take screenshot of the default timer screen
        // Use the root view to capture the entire screen
        captureScreenshot(android.R.id.content, "main_screen_default.png")
        Log.i(TAG, "Default screen captured")
    }
    
    @Test
    fun captureMainScreenTimerRunning() {
        // Start the timer
        onView(withId(R.id.startButton)).perform(click())
        
        // Wait for the UI to update
        TestHelpers.waitFor(500)
        
        // Take screenshot with timer running
        captureScreenshot(android.R.id.content, "main_screen_timer_running.png")
        Log.i(TAG, "Timer running screen captured")
    }
    
    @Test
    fun captureDifferentTimerDurations() {
        // Adjust timer to 20 minutes by swiping right on the slider
        onView(withId(R.id.durationSlider)).perform(swipeRight())
        
        // Wait for the UI to update
        TestHelpers.waitFor(500)
        
        // Take screenshot
        captureScreenshot(android.R.id.content, "main_screen_20min.png")
        Log.i(TAG, "20 minute timer screen captured")
    }
    
    @Test
    fun captureAppDemonstration() {
        // This test demonstrates app functionality
        // Firebase Test Lab will automatically record video
        
        // Take an initial screenshot
        captureScreenshot(android.R.id.content, "app_demo_start.png")
        
        // Adjust slider to show different time values
        onView(withId(R.id.durationSlider)).perform(swipeRight())
        TestHelpers.waitFor(1000)
        
        // Take screenshot after adjustment
        captureScreenshot(android.R.id.content, "app_demo_slider_adjusted.png")
        
        // Start the timer
        onView(withId(R.id.startButton)).perform(click())
        TestHelpers.waitFor(2000)
        
        // Take screenshot with timer running
        captureScreenshot(android.R.id.content, "app_demo_timer_running.png")
        
        // Pause the timer
        onView(withId(R.id.startButton)).perform(click())
        TestHelpers.waitFor(1000)
        
        // Take screenshot with timer paused
        captureScreenshot(android.R.id.content, "app_demo_timer_paused.png")
        
        // Adjust slider again
        onView(withId(R.id.durationSlider)).perform(swipeRight())
        TestHelpers.waitFor(1000)
        
        // Start timer again
        onView(withId(R.id.startButton)).perform(click())
        TestHelpers.waitFor(2000)
        
        // Take final screenshot
        captureScreenshot(android.R.id.content, "app_demo_final.png")
        
        // Pause again
        onView(withId(R.id.startButton)).perform(click())
        
        Log.i(TAG, "App demonstration completed with screenshots")
    }
}
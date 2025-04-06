package dev.broken.app.vibe

import android.os.Environment
import android.util.Log
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream
import android.graphics.Bitmap
import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import org.hamcrest.Matcher
import org.hamcrest.Matchers.any
import java.io.BufferedOutputStream

/**
 * Simple screenshot action for Espresso
 */
fun ViewInteraction.takeScreenshot(name: String): ViewInteraction {
    return perform(object : ViewAction {
        override fun getConstraints(): Matcher<View> = any(View::class.java)
        override fun getDescription(): String = "Take screenshot of '$name'"
        override fun perform(uiController: UiController, view: View) {
            uiController.loopMainThreadUntilIdle()
            
            try {
                // Create a bitmap of the view
                val bitmap = Bitmap.createBitmap(
                    view.width, view.height, Bitmap.Config.ARGB_8888
                )
                val canvas = android.graphics.Canvas(bitmap)
                view.draw(canvas)
                
                // Save the bitmap to multiple locations to increase chances of Firebase finding it
                saveScreenshot(bitmap, name)
                
                Log.i("ScreenshotTest", "Screenshot saved: $name")
            } catch (e: Exception) {
                Log.e("ScreenshotTest", "Failed to take screenshot", e)
            }
        }
        
        private fun saveScreenshot(bitmap: Bitmap, fileName: String) {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            
            // Location 1: Public pictures directory
            try {
                val publicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val screenshotsDir = File(publicDir, "screenshots")
                if (!screenshotsDir.exists()) {
                    screenshotsDir.mkdirs()
                }
                
                val file = File(screenshotsDir, "$fileName.png")
                BufferedOutputStream(FileOutputStream(file)).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                Log.i("ScreenshotTest", "Saved to public Pictures: ${file.absolutePath}")
            } catch (e: Exception) {
                Log.e("ScreenshotTest", "Failed to save to public Pictures", e)
            }
            
            // Location 2: App's external files directory
            try {
                val appFilesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                val screenshotsDir = File(appFilesDir, "screenshots")
                if (!screenshotsDir.exists()) {
                    screenshotsDir.mkdirs()
                }
                
                val file = File(screenshotsDir, "$fileName.png")
                BufferedOutputStream(FileOutputStream(file)).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                Log.i("ScreenshotTest", "Saved to app files: ${file.absolutePath}")
            } catch (e: Exception) {
                Log.e("ScreenshotTest", "Failed to save to app files", e)
            }
            
            // Location 3: Direct sdcard file - simplest approach for Firebase Test Lab
            try {
                val sdcardDir = File("/sdcard", "test_screenshots")
                if (!sdcardDir.exists()) {
                    sdcardDir.mkdirs()
                }
                
                val file = File(sdcardDir, "$fileName.png")
                BufferedOutputStream(FileOutputStream(file)).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                Log.i("ScreenshotTest", "Saved to sdcard: ${file.absolutePath}")
            } catch (e: Exception) {
                Log.e("ScreenshotTest", "Failed to save to sdcard", e)
            }
            
            // Location 4: Internal app data for Firebase
            try {
                val internalDir = File(context.filesDir, "test_screenshots")
                if (!internalDir.exists()) {
                    internalDir.mkdirs()
                }
                
                val file = File(internalDir, "$fileName.png")
                BufferedOutputStream(FileOutputStream(file)).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                Log.i("ScreenshotTest", "Saved to internal: ${file.absolutePath}")
            } catch (e: Exception) {
                Log.e("ScreenshotTest", "Failed to save to internal", e)
            }
        }
    })
}

/**
 * Test for capturing screenshots of the app.
 * Screenshots are saved to multiple locations to maximize visibility to Firebase Test Lab.
 */
@RunWith(AndroidJUnit4::class)
class EspressoScreenshotTest {

    private val TAG = "ScreenshotTest"
    
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    
    @Before
    fun setup() {
        // Configure test environment
        TestHelpers.configureForTesting()
        
        // Wait for UI to stabilize
        TestHelpers.waitFor(1000)
        
        // Log all the places we'll save screenshots
        Log.i(TAG, "Screenshot locations: Public Pictures, App Files, SDCard, and Internal Storage")
    }
    
    @After
    fun tearDown() {
        // Reset test configuration
        TestHelpers.resetTestConfiguration()
    }
    
    @Test
    fun captureMainScreenDefault() {
        // Take screenshot of the default timer screen
        onView(withId(android.R.id.content)).takeScreenshot("main_screen_default")
    }
    
    @Test
    fun captureMainScreenTimerRunning() {
        // Start the timer
        onView(withId(R.id.startButton)).perform(click())
        
        // Wait for the UI to update
        TestHelpers.waitFor(500)
        
        // Take screenshot with timer running
        onView(withId(android.R.id.content)).takeScreenshot("main_screen_timer_running")
    }
    
    @Test
    fun captureDifferentTimerDurations() {
        // Adjust timer to 20 minutes by swiping right on the slider
        onView(withId(R.id.durationSlider)).perform(swipeRight())
        
        // Wait for the UI to update
        TestHelpers.waitFor(500)
        
        // Take screenshot
        onView(withId(android.R.id.content)).takeScreenshot("main_screen_20min")
    }
    
    @Test
    fun captureAppDemonstration() {
        // Take an initial screenshot
        onView(withId(android.R.id.content)).takeScreenshot("app_demo_start")
        
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
        
        // Take final screenshot
        onView(withId(android.R.id.content)).takeScreenshot("app_demo_final")
    }
}
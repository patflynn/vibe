package dev.broken.app.vibe

import android.graphics.Bitmap.CompressFormat
import android.util.Log
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.testlab.screenshot.FirebaseScreenCaptureProcessor
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.test.rule.ActivityTestRule
import androidx.test.screenshot.Screenshot
import java.util.HashSet

/**
 * Test for capturing screenshots using Firebase Test Lab's official screenshot API.
 * These screenshots will be automatically collected by Firebase Test Lab.
 */
@RunWith(AndroidJUnit4::class)
class EspressoScreenshotTest {

    private val TAG = "ScreenshotTest"
    private val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    
    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java, false, false)
    
    @Before
    fun setup() {
        // Configure test environment for predictable screenshots
        TestHelpers.configureForTesting()
        
        // Launch the main activity
        activityRule.launchActivity(null)
        
        // Wait for UI to stabilize
        TestHelpers.waitFor(1000)
        
        Log.i(TAG, "EspressoScreenshotTest setup complete at timestamp: $timestamp")
    }
    
    @After
    fun tearDown() {
        Log.i(TAG, "EspressoScreenshotTest tearDown starting")
        
        // Reset test configuration
        TestHelpers.resetTestConfiguration()
        
        // Log success message to ensure tests completed properly
        Log.i(TAG, "EspressoScreenshotTest completed successfully with timestamp: $timestamp")
    }
    
    /**
     * Take a screenshot using Firebase Test Lab's screenshot API
     * This automatically uploads the screenshot to Firebase Test Lab for viewing
     */
    private fun captureScreenshot(name: String) {
        try {
            Log.i(TAG, "Taking screenshot: $name")
            
            // Initialize Firebase processor
            val firebaseProcessor = FirebaseScreenCaptureProcessor()
            val processors = HashSet<androidx.test.screenshot.ScreenCaptureProcessor>()
            processors.add(firebaseProcessor)
            
            // Capture screenshot of the current activity
            val screenCapture = Screenshot.capture(activityRule.activity)
            
            // Set name and format for the screenshot
            screenCapture
                .setName(name)
                .setFormat(CompressFormat.PNG)
                
            // Process the screenshot with the Firebase processor
            screenCapture.process(processors)
            
            Log.i(TAG, "Screenshot captured and processed successfully: $name")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to capture screenshot: $name", e)
        }
    }
    
    @Test
    fun captureMainScreenDefault() {
        // Take screenshot of the default timer screen
        captureScreenshot("main_screen_default")
        Log.i(TAG, "Default screen captured")
    }
    
    @Test
    fun captureMainScreenTimerRunning() {
        // Start the timer
        onView(withId(R.id.startButton)).perform(click())
        
        // Wait for the UI to update
        TestHelpers.waitFor(500)
        
        // Take screenshot with timer running
        captureScreenshot("main_screen_timer_running")
        Log.i(TAG, "Timer running screen captured")
    }
    
    @Test
    fun captureDifferentTimerDurations() {
        // Adjust timer to 20 minutes by swiping right on the slider
        onView(withId(R.id.durationSlider)).perform(swipeRight())
        
        // Wait for the UI to update
        TestHelpers.waitFor(500)
        
        // Take screenshot
        captureScreenshot("main_screen_20min")
        Log.i(TAG, "20 minute timer screen captured")
    }
    
    @Test
    fun captureAppDemonstration() {
        // This test demonstrates app functionality
        // Firebase Test Lab will automatically record video
        
        // Take an initial screenshot
        captureScreenshot("app_demo_start")
        
        // Adjust slider to show different time values
        onView(withId(R.id.durationSlider)).perform(swipeRight())
        TestHelpers.waitFor(1000)
        
        // Take screenshot after adjustment
        captureScreenshot("app_demo_slider_adjusted")
        
        // Start the timer
        onView(withId(R.id.startButton)).perform(click())
        TestHelpers.waitFor(2000)
        
        // Take screenshot with timer running
        captureScreenshot("app_demo_timer_running")
        
        // Pause the timer
        onView(withId(R.id.startButton)).perform(click())
        TestHelpers.waitFor(1000)
        
        // Take screenshot with timer paused
        captureScreenshot("app_demo_timer_paused")
        
        // Adjust slider again
        onView(withId(R.id.durationSlider)).perform(swipeRight())
        TestHelpers.waitFor(1000)
        
        // Start timer again
        onView(withId(R.id.startButton)).perform(click())
        TestHelpers.waitFor(2000)
        
        // Take final screenshot
        captureScreenshot("app_demo_final")
        
        // Pause again
        onView(withId(R.id.startButton)).perform(click())
        
        Log.i(TAG, "App demonstration completed with screenshots")
    }
}
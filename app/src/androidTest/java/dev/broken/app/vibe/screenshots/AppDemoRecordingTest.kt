package dev.broken.app.vibe.screenshots

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.broken.app.vibe.MainActivity
import dev.broken.app.vibe.R
import dev.broken.app.vibe.TestHelpers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test class that records a demonstration video of the app in use.
 * This will be used for the project README and documentation.
 */
@RunWith(AndroidJUnit4::class)
class AppDemoRecordingTest {

    private lateinit var activityScenario: ActivityScenario<MainActivity>
    private val screenRecorder = ScreenRecordingHelper()

    @Before
    fun setup() {
        // Configure test environment for demo recording
        TestHelpers.configureForTesting()
        
        // Initialize screenshot helper
        ScreenshotHelper.initialize()
        
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
    fun recordAppDemonstration() {
        // Start screen recording
        screenRecorder.startRecording(
            filename = "vibe_app_demo",
            timeLimit = 20 // 20 seconds max for the demo
        )
        
        try {
            // Wait for UI to stabilize at the beginning
            TestHelpers.waitFor(2000)
            
            // Adjust slider to show different time values
            onView(withId(R.id.durationSlider)).perform(swipeRight())
            TestHelpers.waitFor(1500)
            
            // Start the timer
            onView(withId(R.id.startButton)).perform(click())
            TestHelpers.waitFor(3000)
            
            // Pause the timer
            onView(withId(R.id.startButton)).perform(click())
            TestHelpers.waitFor(1500)
            
            // Adjust slider again
            onView(withId(R.id.durationSlider)).perform(swipeRight())
            TestHelpers.waitFor(1500)
            
            // Start timer again
            onView(withId(R.id.startButton)).perform(click())
            TestHelpers.waitFor(3000)
            
            // Pause again
            onView(withId(R.id.startButton)).perform(click())
            TestHelpers.waitFor(2000)
            
        } finally {
            // Stop recording regardless of any test failures
            screenRecorder.stopRecording()
        }
    }
}
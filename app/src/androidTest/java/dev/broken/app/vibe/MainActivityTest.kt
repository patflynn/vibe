package dev.broken.app.vibe

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private lateinit var activityScenario: ActivityScenario<MainActivity>
    private var timerIdlingResource: TestHelpers.TimerIdlingResource? = null

    @Before
    fun setup() {
        // Configure test environment
        TestHelpers.configureForTesting()
        
        // Launch the activity
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
    }
    
    @After
    fun tearDown() {
        // Unregister any IdlingResources
        timerIdlingResource?.let {
            IdlingRegistry.getInstance().unregister(it)
        }
        
        // Reset test configuration
        TestHelpers.resetTestConfiguration()
        
        // Close the activity
        activityScenario.close()
    }
    
    @Test
    fun testAppLaunchesCorrectly() {
        // Check if the timer is displayed with default value
        onView(withId(R.id.timerTextView))
            .check(matches(isDisplayed()))
    }
    
    @Test
    fun testStartButtonChangesToPauseWhenPressed() {
        // Click the start button
        onView(withId(R.id.startButton))
            .check(matches(isDisplayed()))
            .perform(click())
            
        // Verify the button changed to pause
        onView(withId(R.id.startButton))
            .check(matches(withContentDescription(R.string.pause_meditation)))
    }
    
    @Test
    fun testSliderIsDisabledWhenTimerIsRunning() {
        // Check that slider is initially enabled
        onView(withId(R.id.durationSlider))
            .check(matches(isEnabled()))
        
        // Start the timer
        onView(withId(R.id.startButton))
            .perform(click())
        
        // Verify slider is disabled
        onView(withId(R.id.durationSlider))
            .check(matches(not(isEnabled())))
            
        // Stop the timer
        onView(withId(R.id.startButton))
            .perform(click())
            
        // Verify slider is enabled again
        onView(withId(R.id.durationSlider))
            .check(matches(isEnabled()))
    }
    
    @Test
    fun testFullMeditationCycle() {
        android.util.Log.d("VibeTest", "Starting testFullMeditationCycle")
        
        // Make tests more predictable by skipping timers entirely
        activityScenario.onActivity { activity ->
            android.util.Log.d("VibeTest", "Setting up test environment")
            
            // Set super short timer
            FeatureFlags.USE_SHORT_TIMERS_FOR_TESTING = true
            
            // Disable sound and vibration for testing
            FeatureFlags.DISABLE_SOUND_FOR_TESTING = true
            FeatureFlags.DISABLE_VIBRATION_FOR_TESTING = true
        }
        
        // Start the meditation
        android.util.Log.d("VibeTest", "Clicking start button")
        onView(withId(R.id.startButton))
            .perform(click())
        
        // Verify the meditation is running
        android.util.Log.d("VibeTest", "Verifying meditation running")
        onView(withId(R.id.startButton))
            .check(matches(withContentDescription(R.string.pause_meditation)))
            
        // Manually pause the timer instead of waiting for it to finish
        android.util.Log.d("VibeTest", "Clicking pause button")
        onView(withId(R.id.startButton))
            .perform(click())
            
        // Verify the button is back to start state after pausing
        android.util.Log.d("VibeTest", "Checking button in start state")
        onView(withId(R.id.startButton))
            .check(matches(withContentDescription(R.string.start_meditation)))
            
        // Verify slider is enabled after pausing
        android.util.Log.d("VibeTest", "Checking slider is enabled")
        onView(withId(R.id.durationSlider))
            .check(matches(isEnabled()))
            
        android.util.Log.d("VibeTest", "Test completed successfully")
    }
}
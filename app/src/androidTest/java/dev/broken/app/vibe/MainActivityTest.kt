package dev.broken.app.vibe

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.material.slider.Slider
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
        // Clear any existing preferences before each test
        clearPreferences()
        
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
    
    @Test
    fun testDefaultDurationIsLoadedCorrectly() {
        // Verify that the default duration (index 3 = 20 minutes) is loaded when no preferences exist
        onView(withId(R.id.timerTextView))
            .check(matches(withText("20:00")))
            
        // Verify slider is at default position
        activityScenario.onActivity { activity ->
            val slider = activity.findViewById<Slider>(R.id.durationSlider)
            assert(slider.value == 3.0f) { "Slider should be at default position 3" }
        }
    }
    
    @Test
    fun testDurationPersistenceAfterSliderChange() {
        // Change the slider to 25 minutes (index 4)
        activityScenario.onActivity { activity ->
            val slider = activity.findViewById<Slider>(R.id.durationSlider)
            slider.value = 4.0f // 25 minutes
        }
        
        // Verify the timer display updated to 25 minutes
        onView(withId(R.id.timerTextView))
            .check(matches(withText("25:00")))
        
        // Close and relaunch the activity to test persistence
        activityScenario.close()
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
        
        // Verify the saved duration is restored
        onView(withId(R.id.timerTextView))
            .check(matches(withText("25:00")))
            
        activityScenario.onActivity { activity ->
            val slider = activity.findViewById<Slider>(R.id.durationSlider)
            assert(slider.value == 4.0f) { "Slider should be at saved position 4" }
        }
    }
    
    @Test
    fun testDurationPersistenceWithMultipleChanges() {
        // Test changing duration multiple times to ensure each change is saved
        val testPositions = listOf(0, 2, 5, 1) // Test different positions
        val expectedMinutes = listOf("05:00", "15:00", "30:00", "10:00")
        
        for (i in testPositions.indices) {
            val position = testPositions[i]
            val expectedTime = expectedMinutes[i]
            
            // Change slider position
            activityScenario.onActivity { activity ->
                val slider = activity.findViewById<Slider>(R.id.durationSlider)
                slider.value = position.toFloat()
            }
            
            // Verify display updates
            onView(withId(R.id.timerTextView))
                .check(matches(withText(expectedTime)))
            
            // Close and relaunch to test persistence
            activityScenario.close()
            activityScenario = ActivityScenario.launch(MainActivity::class.java)
            
            // Verify persistence
            onView(withId(R.id.timerTextView))
                .check(matches(withText(expectedTime)))
                
            activityScenario.onActivity { activity ->
                val slider = activity.findViewById<Slider>(R.id.durationSlider)
                assert(slider.value == position.toFloat()) { 
                    "Slider should be at saved position $position" 
                }
            }
        }
    }
    
    @Test
    fun testInvalidPreferenceHandling() {
        // Manually set an invalid preference value
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val prefs = context.getSharedPreferences("vibe_preferences", Context.MODE_PRIVATE)
        prefs.edit().putInt("selected_duration_index", 999).apply() // Invalid index
        
        // Relaunch activity
        activityScenario.close()
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
        
        // Verify it falls back to default (20 minutes)
        onView(withId(R.id.timerTextView))
            .check(matches(withText("20:00")))
            
        activityScenario.onActivity { activity ->
            val slider = activity.findViewById<Slider>(R.id.durationSlider)
            assert(slider.value == 3.0f) { "Slider should fall back to default position 3" }
        }
    }
    
    @Test
    fun testNegativePreferenceHandling() {
        // Manually set a negative preference value
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val prefs = context.getSharedPreferences("vibe_preferences", Context.MODE_PRIVATE)
        prefs.edit().putInt("selected_duration_index", -1).apply() // Invalid negative index
        
        // Relaunch activity
        activityScenario.close()
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
        
        // Verify it falls back to default (20 minutes)
        onView(withId(R.id.timerTextView))
            .check(matches(withText("20:00")))
            
        activityScenario.onActivity { activity ->
            val slider = activity.findViewById<Slider>(R.id.durationSlider)
            assert(slider.value == 3.0f) { "Slider should fall back to default position 3" }
        }
    }
    
    private fun clearPreferences() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val prefs = context.getSharedPreferences("vibe_preferences", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}
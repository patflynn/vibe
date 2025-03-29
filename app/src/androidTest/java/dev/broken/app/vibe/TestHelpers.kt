package dev.broken.app.vibe

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.IdlingRegistry
import java.util.concurrent.TimeUnit

/**
 * Helper class to enable test-specific features and flags
 */
object TestHelpers {
    /**
     * Set up app for testing with shorter timers and disabled audio/vibration
     */
    fun configureForTesting() {
        // Disable sound and vibration to avoid test flakiness
        FeatureFlags.DISABLE_SOUND_FOR_TESTING = true
        FeatureFlags.DISABLE_VIBRATION_FOR_TESTING = true
        
        // Use shorter timers for faster tests
        FeatureFlags.USE_SHORT_TIMERS_FOR_TESTING = true
        
        // Enable logging for better test diagnostics
        FeatureFlags.LOG_TIMER_EVENTS = true
    }
    
    /**
     * Reset flags to default values after testing
     */
    fun resetTestConfiguration() {
        FeatureFlags.DISABLE_SOUND_FOR_TESTING = false
        FeatureFlags.DISABLE_VIBRATION_FOR_TESTING = false
        FeatureFlags.USE_SHORT_TIMERS_FOR_TESTING = false
        FeatureFlags.LOG_TIMER_EVENTS = false
    }
    
    /**
     * Custom IdlingResource for waiting for timer to complete
     */
    class TimerIdlingResource(private val mainActivity: MainActivity) : IdlingResource {
        private var callback: IdlingResource.ResourceCallback? = null
        private var timeoutFallback: Boolean = false
        private val startTime = System.currentTimeMillis()
        private val MAX_WAIT_TIME_MS = 30000L // 30 seconds max wait
        
        init {
            android.util.Log.d("VibeTest", "TimerIdlingResource initialized")
            // Start a fallback thread to prevent tests from hanging indefinitely
            Thread {
                try {
                    Thread.sleep(MAX_WAIT_TIME_MS)
                    timeoutFallback = true
                    android.util.Log.w("VibeTest", "TimerIdlingResource timeout exceeded, forcing idle state")
                    callback?.onTransitionToIdle()
                } catch (e: InterruptedException) {
                    // Ignore
                }
            }.start()
        }

        override fun getName(): String = "Timer Idling Resource"

        override fun isIdleNow(): Boolean {
            val isTimerRunning = mainActivity.isTimerRunning
            val elapsedTime = System.currentTimeMillis() - startTime
            
            // Consider idle if timer not running or if timeout occurred
            val isIdle = !isTimerRunning || timeoutFallback
            
            android.util.Log.d("VibeTest", "IdlingResource check: isTimerRunning=$isTimerRunning, " +
                    "isIdle=$isIdle, elapsedTime=${elapsedTime}ms, timeoutFallback=$timeoutFallback")
            
            if (isIdle) {
                android.util.Log.d("VibeTest", "Transitioning to idle state")
                callback?.onTransitionToIdle()
            }
            
            return isIdle
        }

        override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
            android.util.Log.d("VibeTest", "IdlingResource callback registered")
            this.callback = callback
        }
    }
    
    /**
     * Safely wait for a specific time during tests
     */
    fun waitFor(timeMs: Long) {
        try {
            Thread.sleep(timeMs)
        } catch (e: InterruptedException) {
            // Ignored
        }
    }
}
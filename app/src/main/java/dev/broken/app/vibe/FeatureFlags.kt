package dev.broken.app.vibe

/**
 * Feature flag configuration for the application
 * Allows toggling features at runtime and simplifies testing
 */
object FeatureFlags {
    // Testing flags
    var DISABLE_SOUND_FOR_TESTING = false
    var DISABLE_VIBRATION_FOR_TESTING = false
    var USE_SHORT_TIMERS_FOR_TESTING = false
    var LOG_TIMER_EVENTS = false
    
    // Timer intervals in minutes (default options)
    val TIMER_INTERVALS = listOf(5, 10, 15, 20, 25, 30, 40)
    
    // Get the duration in milliseconds for a timer value
    fun getDurationInMillis(timerValue: Int): Long {
        // For testing, we want to use short timers (just a few seconds)
        if (USE_SHORT_TIMERS_FOR_TESTING) {
            // When testing, use seconds instead of minutes (5 = 5 seconds)
            return timerValue * 1000L
        }
        
        // Regular usage: convert minutes to milliseconds
        return timerValue * 60 * 1000L
    }
}
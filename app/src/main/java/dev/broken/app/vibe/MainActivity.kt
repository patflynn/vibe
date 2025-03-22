package dev.broken.app.vibe

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import dev.broken.app.vibe.databinding.ActivityMainBinding
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Feature flags for testing different app behaviors
 */
object FeatureFlags {
    var DISABLE_SOUND_FOR_TESTING = false
    var DISABLE_VIBRATION_FOR_TESTING = false
    var USE_SHORT_TIMERS_FOR_TESTING = false
    var LOG_TIMER_EVENTS = false
}

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var timer: CountDownTimer? = null
    
    // Changed to internal for testing access
    internal var isTimerRunning = false
    
    private var mediaPlayer: MediaPlayer? = null
    
    // Define meditation durations in minutes
    private val durations = listOf(5, 10, 15, 20, 25, 30, 40)
    private var selectedDurationIndex = 3 // Default is 20 minutes (index 3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Ensure the screen stays on during the entire app lifecycle
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setupSlider()
        setupStartButton()
    }

    private fun setupSlider() {
        binding.durationSlider.apply {
            value = selectedDurationIndex.toFloat()
            addOnChangeListener { _, value, _ ->
                selectedDurationIndex = value.toInt()
                updateTimerDisplay(durations[selectedDurationIndex])
            }
        }
        
        // Set initial timer display
        updateTimerDisplay(durations[selectedDurationIndex])
    }

    private fun setupStartButton() {
        binding.startButton.setOnClickListener {
            if (isTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }
    }

    private fun startTimer() {
        // Play start sound
        playBellSound()
        
        val durationMinutes = durations[selectedDurationIndex]
        var durationMillis = TimeUnit.MINUTES.toMillis(durationMinutes.toLong())
        
        // Use shorter timers for testing if flag is enabled
        if (FeatureFlags.USE_SHORT_TIMERS_FOR_TESTING) {
            durationMillis = TimeUnit.SECONDS.toMillis(10) // Use 10 seconds for testing
        }
        
        if (FeatureFlags.LOG_TIMER_EVENTS) {
            android.util.Log.d("VibeApp", "Starting timer for ${durationMillis}ms")
        }
        
        timer = object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                binding.timerTextView.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
                
                if (FeatureFlags.LOG_TIMER_EVENTS) {
                    android.util.Log.d("VibeApp", "Timer tick: ${minutes}m ${seconds}s remaining")
                }
            }

            override fun onFinish() {
                binding.timerTextView.text = getString(R.string._00_00)
                resetTimerUI()
                // Play end sound
                playBellSound()
                
                if (FeatureFlags.LOG_TIMER_EVENTS) {
                    android.util.Log.d("VibeApp", "Timer finished")
                }
            }
        }.start()
        
        isTimerRunning = true
        updateTimerUI()
    }

    private fun pauseTimer() {
        timer?.cancel()
        isTimerRunning = false
        updateTimerUI()
    }

    private fun resetTimerUI() {
        isTimerRunning = false
        updateTimerUI()
        updateTimerDisplay(durations[selectedDurationIndex])
    }
    
    private fun updateTimerUI() {
        if (isTimerRunning) {
            binding.startButton.setImageResource(R.drawable.ic_pause)
            binding.startButton.contentDescription = getString(R.string.pause_meditation)
            binding.durationSlider.isEnabled = false
        } else {
            binding.startButton.setImageResource(R.drawable.ic_play)
            binding.startButton.contentDescription = getString(R.string.start_meditation)
            binding.durationSlider.isEnabled = true
        }
    }
    
    private fun updateTimerDisplay(minutes: Int) {
        binding.timerTextView.text = String.format(Locale.getDefault(),"%02d:00", minutes)
    }
    
    private fun playBellSound() {
        // Skip playing sounds if disabled for testing
        if (!FeatureFlags.DISABLE_SOUND_FOR_TESTING) {
            // Release any existing MediaPlayer
            mediaPlayer?.release()
            
            // Create and prepare a new MediaPlayer
            mediaPlayer = MediaPlayer.create(this, R.raw.med_bell)
            mediaPlayer?.start()
            
            if (FeatureFlags.LOG_TIMER_EVENTS) {
                android.util.Log.d("VibeApp", "Bell sound played")
            }
        }
        
        // Skip vibration if disabled for testing
        if (!FeatureFlags.DISABLE_VIBRATION_FOR_TESTING) {
            // Also vibrate the device gently
            val vibrator = getSystemService(Vibrator::class.java) as Vibrator
            if (vibrator.hasVibrator()) {
                // Create a gentle vibration pattern - 500ms vibration, 200ms pause, 500ms vibration
                val vibrationPattern = longArrayOf(0, 500, 200, 500)
                vibrator.vibrate(VibrationEffect.createWaveform(vibrationPattern, -1))
                
                if (FeatureFlags.LOG_TIMER_EVENTS) {
                    android.util.Log.d("VibeApp", "Device vibration triggered")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
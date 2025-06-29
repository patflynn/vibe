package dev.broken.app.vibe

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.VibratorManager
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import dev.broken.app.vibe.databinding.ActivityMainBinding
import java.util.Locale
import java.util.concurrent.TimeUnit

// FeatureFlags moved to separate file

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var timer: CountDownTimer? = null
    
    // Changed to internal for testing access
    internal var isTimerRunning = false
    
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var sharedPreferences: SharedPreferences
    
    // Use durations from FeatureFlags
    private val durations = FeatureFlags.TIMER_INTERVALS
    private var selectedDurationIndex = 3 // Default is 20 minutes (index 3)
    
    companion object {
        private const val PREFS_NAME = "vibe_preferences"
        private const val KEY_SELECTED_DURATION_INDEX = "selected_duration_index"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        
        // Load saved duration preference
        loadSavedDuration()
        
        // Ensure the screen stays on during the entire app lifecycle
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setupSlider()
        setupStartButton()
    }

    private fun loadSavedDuration() {
        selectedDurationIndex = sharedPreferences.getInt(KEY_SELECTED_DURATION_INDEX, 3)
        // Ensure the index is within valid bounds
        if (selectedDurationIndex < 0 || selectedDurationIndex >= durations.size) {
            selectedDurationIndex = 3 // Default to 20 minutes if invalid
        }
    }
    
    private fun saveDuration() {
        sharedPreferences.edit()
            .putInt(KEY_SELECTED_DURATION_INDEX, selectedDurationIndex)
            .apply()
    }

    private fun setupSlider() {
        binding.durationSlider.apply {
            value = selectedDurationIndex.toFloat()
            addOnChangeListener { _, value, _ ->
                selectedDurationIndex = value.toInt()
                updateTimerDisplay(durations[selectedDurationIndex])
                saveDuration()
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
        var durationMillis = FeatureFlags.getDurationInMillis(durationMinutes)
        
        if (FeatureFlags.LOG_TIMER_EVENTS) {
            android.util.Log.d("VibeApp", "Starting timer for ${durationMillis}ms")
        }
        
        timer = object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                binding.timerTextView.text = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds)
                
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
        binding.timerTextView.text = String.format(Locale.getDefault(), "%02d:00", minutes)
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
            vibrateDevice()
        }
    }
    
    /**
     * Handles device vibration with backward compatibility for different API levels
     */
    private fun vibrateDevice() {
        // Create a gentle vibration pattern - 500ms vibration, 200ms pause, 500ms vibration
        val vibrationPattern = longArrayOf(0, 500, 200, 500)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // For Android 12+ (API 31+), use VibratorManager
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.defaultVibrator
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(VibrationEffect.createWaveform(vibrationPattern, -1))
                
                if (FeatureFlags.LOG_TIMER_EVENTS) {
                    android.util.Log.d("VibeApp", "Device vibration triggered (S+)")
                }
            }
        } else {
            // For Android 11 and below (API 30-), use the deprecated Vibrator directly
            @Suppress("DEPRECATION")
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as android.os.Vibrator
            if (vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // For Android 8.0+ use VibrationEffect
                    vibrator.vibrate(VibrationEffect.createWaveform(vibrationPattern, -1))
                } else {
                    // For very old devices, use the deprecated vibrate method
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(vibrationPattern, -1)
                }
                
                if (FeatureFlags.LOG_TIMER_EVENTS) {
                    android.util.Log.d("VibeApp", "Device vibration triggered (legacy)")
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
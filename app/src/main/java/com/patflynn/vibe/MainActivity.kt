package com.patflynn.vibe

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.patflynn.vibe.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var timer: CountDownTimer? = null
    private var isTimerRunning = false
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
        val durationMillis = TimeUnit.MINUTES.toMillis(durationMinutes.toLong())
        
        timer = object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                binding.timerTextView.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                binding.timerTextView.text = "00:00"
                resetTimerUI()
                // Play end sound
                playBellSound()
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
        binding.timerTextView.text = String.format("%02d:00", minutes)
    }
    
    private fun playBellSound() {
        // Release any existing MediaPlayer
        mediaPlayer?.release()
        
        // Create and prepare a new MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.meditation_bell)
        mediaPlayer?.start()
        
        // Also vibrate the device gently
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            // Create a gentle vibration pattern - 500ms vibration, 200ms pause, 500ms vibration
            val vibrationPattern = longArrayOf(0, 500, 200, 500)
            vibrator.vibrate(VibrationEffect.createWaveform(vibrationPattern, -1))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
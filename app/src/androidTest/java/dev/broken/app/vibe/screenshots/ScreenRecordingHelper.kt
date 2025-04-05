package dev.broken.app.vibe.screenshots

import android.os.Environment
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Helper class for screen recording during tests
 */
class ScreenRecordingHelper {
    private var recordingProcess: Process? = null
    private var outputFile: File? = null
    private var pfd: ParcelFileDescriptor? = null
    private val TAG = "ScreenRecordingHelper"
    
    /**
     * Start screen recording
     * @param filename Base filename for the recording (without extension)
     * @param width Width of the video (default 720)
     * @param height Height of the video (default 1280)
     * @param timeLimit Time limit in seconds (default 30)
     * @param bitRate Bit rate in Mbps (default 4)
     * @return True if recording started successfully
     */
    fun startRecording(
        filename: String,
        width: Int = 720,
        height: Int = 1280,
        timeLimit: Int = 30,
        bitRate: Int = 4
    ): Boolean {
        try {
            // Create output file
            outputFile = createOutputFile(filename)
            
            // Get UI device instance
            val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            
            // Build the command for screen recording
            val command = "screenrecord --size ${width}x${height} " +
                          "--time-limit $timeLimit " +
                          "--bit-rate ${bitRate}000000 " +
                          "${outputFile?.absolutePath}"
            
            // Start the recording process
            recordingProcess = Runtime.getRuntime().exec(command)
            
            Log.d(TAG, "Screen recording started: ${outputFile?.absolutePath}")
            return true
        } catch (e: IOException) {
            Log.e(TAG, "Failed to start screen recording: ${e.message}")
            return false
        }
    }
    
    /**
     * Stop the current screen recording
     * @return Path to the recorded file or null if recording failed
     */
    fun stopRecording(): String? {
        try {
            // Send SIGINT to the process to stop recording gracefully
            recordingProcess?.let {
                if (it.isAlive) {
                    it.destroy()
                    it.waitFor()
                }
            }
            
            // Close file descriptor if open
            pfd?.close()
            
            Log.d(TAG, "Screen recording stopped: ${outputFile?.absolutePath}")
            return outputFile?.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping screen recording: ${e.message}")
            return null
        } finally {
            recordingProcess = null
            pfd = null
        }
    }
    
    /**
     * Create output file for the recording
     */
    private fun createOutputFile(filename: String): File {
        // Get the external storage directory for movies
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        val screenRecordingDir = File(storageDir, "screenshots")
        
        // Ensure the directory exists
        if (!screenRecordingDir.exists()) {
            screenRecordingDir.mkdirs()
        }
        
        // Create a timestamp for unique filenames
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        
        // Create the file
        return File(screenRecordingDir, "${filename}_${timeStamp}.mp4")
    }
}
package com.steadymate.app.ui.utils

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.steadymate.app.ui.screens.tools.BreathingExerciseType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages breathing exercise audio playback with speed adjustment capabilities
 */
@Singleton
class SoundManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val tag = "SoundManager"
    
    private var mediaPlayer: MediaPlayer? = null
    private var audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    
    private val _volume = MutableStateFlow(0.7f) // Default volume
    val volume: StateFlow<Float> = _volume.asStateFlow()
    
    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()
    
    // Audio focus change listener
    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                // Lost focus for an unbounded amount of time: stop playback
                stopSound()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                // Lost focus for a short time: pause playback
                pauseSound()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                // Lost focus but can duck: lower the volume
                setVolume(_volume.value * 0.3f)
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                // Gained focus: restore volume and resume if needed
                setVolume(_volume.value)
                // Could resume here if we were ducking
            }
        }
    }
    
    /**
     * Initialize and start playing the breathing sound
     */
    fun startBreathingSound() {
        try {
            // Request audio focus first
            if (!requestAudioFocus()) {
                Log.w(tag, "Could not gain audio focus")
                return
            }
            
            // Initialize MediaPlayer if not already done
            if (mediaPlayer == null) {
                initializeMediaPlayer()
            }
            
            mediaPlayer?.let { player ->
                if (!player.isPlaying) {
                    player.start()
                    _isPlaying.value = true
                    Log.d(tag, "Started breathing sound playback")
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error starting breathing sound", e)
            _isPlaying.value = false
        }
    }
    
    /**
     * Pause the breathing sound
     */
    fun pauseSound() {
        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.pause()
                    _isPlaying.value = false
                    Log.d(tag, "Paused breathing sound")
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error pausing sound", e)
        }
    }
    
    /**
     * Resume the breathing sound
     */
    fun resumeSound() {
        try {
            mediaPlayer?.let { player ->
                if (!player.isPlaying) {
                    player.start()
                    _isPlaying.value = true
                    Log.d(tag, "Resumed breathing sound")
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error resuming sound", e)
        }
    }
    
    /**
     * Stop the breathing sound and release resources
     */
    fun stopSound() {
        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.stop()
                }
                player.reset()
                _isPlaying.value = false
                Log.d(tag, "Stopped breathing sound")
            }
            releaseAudioFocus()
        } catch (e: Exception) {
            Log.e(tag, "Error stopping sound", e)
        }
    }
    
    /**
     * Set the playback speed to match breathing exercise timing
     * @param speed Playback speed (0.5 = half speed, 2.0 = double speed)
     */
    fun setPlaybackSpeed(speed: Float) {
        try {
            val clampedSpeed = speed.coerceIn(0.25f, 3.0f) // Reasonable speed range
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mediaPlayer?.playbackParams = mediaPlayer?.playbackParams?.setSpeed(clampedSpeed) ?: return
                _playbackSpeed.value = clampedSpeed
                Log.d(tag, "Set playback speed to ${clampedSpeed}x")
            } else {
                Log.w(tag, "Playback speed adjustment not supported on this Android version")
            }
        } catch (e: Exception) {
            Log.e(tag, "Error setting playback speed", e)
        }
    }
    
    /**
     * Calculate and set playback speed based on breathing exercise timing
     * @param exerciseCycleDurationSeconds Total duration of one breathing cycle
     * @param originalSoundDurationSeconds Duration of the original sound file
     */
    fun adjustSpeedForExercise(exerciseCycleDurationSeconds: Float, originalSoundDurationSeconds: Float = 10f) {
        val speedRatio = originalSoundDurationSeconds / exerciseCycleDurationSeconds
        setPlaybackSpeed(speedRatio)
        Log.d(tag, "Adjusted speed for exercise: ${exerciseCycleDurationSeconds}s cycle, speed: ${speedRatio}x")
    }
    
    /**
     * Set volume level
     * @param volumeLevel Volume level (0.0 to 1.0)
     */
    fun setVolume(volumeLevel: Float) {
        try {
            val clampedVolume = volumeLevel.coerceIn(0f, 1f)
            mediaPlayer?.setVolume(clampedVolume, clampedVolume)
            _volume.value = clampedVolume
            Log.d(tag, "Set volume to $clampedVolume")
        } catch (e: Exception) {
            Log.e(tag, "Error setting volume", e)
        }
    }
    
    /**
     * Initialize MediaPlayer with the breathing sound file
     */
    private fun initializeMediaPlayer() {
        try {
            val assetFd: AssetFileDescriptor = context.assets.openFd("audio/breathe.mp3")
            
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                
                setDataSource(assetFd.fileDescriptor, assetFd.startOffset, assetFd.length)
                assetFd.close()
                
                isLooping = true // Loop the breathing sound continuously
                prepare()
                setVolume(_volume.value, _volume.value)
            }
            
            Log.d(tag, "MediaPlayer initialized successfully")
        } catch (e: Exception) {
            Log.e(tag, "Error initializing MediaPlayer", e)
            mediaPlayer = null
        }
    }
    
    /**
     * Request audio focus for playback
     */
    private fun requestAudioFocus(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requestAudioFocusModern()
        } else {
            requestAudioFocusLegacy()
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestAudioFocusModern(): Boolean {
        audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            .setAcceptsDelayedFocusGain(true)
            .setOnAudioFocusChangeListener(audioFocusChangeListener)
            .build()
        
        val result = audioManager.requestAudioFocus(audioFocusRequest!!)
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }
    
    @Suppress("DEPRECATION")
    private fun requestAudioFocusLegacy(): Boolean {
        val result = audioManager.requestAudioFocus(
            audioFocusChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }
    
    /**
     * Release audio focus
     */
    private fun releaseAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && audioFocusRequest != null) {
            audioManager.abandonAudioFocusRequest(audioFocusRequest!!)
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(audioFocusChangeListener)
        }
    }
    
    /**
     * Release all resources
     */
    fun release() {
        try {
            stopSound()
            mediaPlayer?.release()
            mediaPlayer = null
            Log.d(tag, "SoundManager resources released")
        } catch (e: Exception) {
            Log.e(tag, "Error releasing resources", e)
        }
    }
    
    /**
     * Check if audio is available on this device
     */
    val isAudioAvailable: Boolean
        get() = try {
            context.assets.openFd("audio/breathe.mp3").use { true }
        } catch (e: Exception) {
            Log.w(tag, "Breathing audio file not found", e)
            false
        }
}

/**
 * Extension function to calculate breathing cycle duration
 */
fun BreathingExerciseType.getCycleDuration(): Float {
    return when (this) {
        BreathingExerciseType.BOX -> 16f // 4+4+4+4 seconds
        BreathingExerciseType.FOUR_SEVEN_EIGHT -> 19f // 4+7+8 seconds
        BreathingExerciseType.SIMPLE -> 10f // 4+6 seconds
    }
}

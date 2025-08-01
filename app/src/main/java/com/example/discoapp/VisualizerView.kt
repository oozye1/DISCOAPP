package com.example.discoapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.AttributeSet
import android.view.View
import com.example.discoapp.model.VisualizerEffect
import com.example.discoapp.model.VisualizerShape
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * A custom view that visualizes audio data captured from the device's microphone. The view
 * supports three distinct shapes—bars, radial spokes and a waveform—whose appearance and
 * behaviour are controlled through instances of [VisualizerEffect].
 *
 * The view manages an underlying [AudioRecord] instance to capture raw audio data. When a new
 * effect is set via [setEffect], the view resets its renderer and applies the new colours,
 * bar count and amplitude multiplier. When the view is detached from the window it releases
 * the audio resources to conserve power.
 */
class VisualizerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var audioRecord: AudioRecord? = null
    private var waveform: ByteArray = ByteArray(0)
    private var effect: VisualizerEffect? = null
    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    private var gradient: LinearGradient? = null
    private var captureThread: Thread? = null
    private var isCapturing = false

    /**
     * Assign an effect to this view and initialize audio capture if necessary. This method
     * should be called after the RECORD_AUDIO permission has been granted. Setting a new effect
     * replaces any existing effect and resets the colour gradient.
     */
    fun setEffect(effect: VisualizerEffect) {
        this.effect = effect
        gradient = null
        invalidate()
    }

    /**
     * Set up and start the [AudioRecord] instance to capture audio from the microphone. Audio
     * data is read in a background thread to avoid blocking the UI.
     */
    @SuppressLint("MissingPermission")
    fun start() {
        if (isCapturing) return
        try {
            val sampleRate = 44100
            val channelConfig = AudioFormat.CHANNEL_IN_MONO
            val audioFormat = AudioFormat.ENCODING_PCM_8BIT
            val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat).coerceAtLeast(1024)

            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                audioRecord = null
                return
            }

            waveform = ByteArray(bufferSize)
            audioRecord?.startRecording()

            isCapturing = true
            captureThread = Thread {
                while (isCapturing) {
                    audioRecord?.read(waveform, 0, waveform.size)
                    postInvalidate()
                }
            }
            captureThread?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        if (!isCapturing) return
        isCapturing = false
        try {
            captureThread?.join(100)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
        captureThread = null
        if (audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
            audioRecord?.stop()
        }
        audioRecord?.release()
        audioRecord = null
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stop()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rebuildGradient()
    }

    /** Recreate a linear gradient across the width of the view using the current effect colours. */
    private fun rebuildGradient() {
        val eff = effect ?: return
        if (eff.colors.size > 1) {
            val positions = FloatArray(eff.colors.size) { i -> i.toFloat() / (eff.colors.size - 1) }
            gradient = LinearGradient(
                0f, 0f, width.toFloat(), 0f,
                eff.colors, positions, Shader.TileMode.CLAMP
            )
        } else {
            gradient = null
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val eff = effect ?: return
        if (waveform.isEmpty() || !isCapturing) return
        when (eff.shape) {
            VisualizerShape.BARS -> drawBars(canvas, eff)
            VisualizerShape.RADIAL -> drawRadial(canvas, eff)
            VisualizerShape.WAVE -> drawWave(canvas, eff)
        }
    }

    /** Draws a vertical bar graph where each bar height corresponds to the amplitude of a sample. */
    private fun drawBars(canvas: Canvas, eff: VisualizerEffect) {
        val barCount = eff.barCount
        if (barCount <= 0) return
        val barWidth = width.toFloat() / barCount
        val centerY = height.toFloat()
        for (i in 0 until barCount) {
            val index = (waveform.size * i) / barCount
            val value = waveform[index].toInt() and 0xFF
            val amplitude = abs(value - 128) / 128f * centerY * eff.amplitudeMultiplier
            val left = i * barWidth
            val right = left + barWidth * 0.8f
            val top = centerY - amplitude
            paint.color = eff.colors[i % eff.colors.size]
            canvas.drawRect(left, top, right, centerY, paint)
        }
    }

    /** Draws radial spokes emanating from the center of the view based on amplitude. */
    private fun drawRadial(canvas: Canvas, eff: VisualizerEffect) {
        val barCount = eff.barCount
        val centerX = width / 2f
        val centerY = height / 2f
        val maxRadius = min(width, height) / 2f
        paint.strokeWidth = 4f
        for (i in 0 until barCount) {
            val index = (waveform.size * i) / barCount
            val value = waveform[index].toInt() and 0xFF
            val amplitude = abs(value - 128) / 128f * maxRadius * eff.amplitudeMultiplier
            val angle = (2.0 * Math.PI * i / barCount).toFloat()
            val endX = centerX + amplitude * cos(angle)
            val endY = centerY + amplitude * sin(angle)
            paint.color = eff.colors[i % eff.colors.size]
            canvas.drawLine(centerX, centerY, endX, endY, paint)
        }
    }

    /** Draws a continuous waveform across the width of the view. */
    private fun drawWave(canvas: Canvas, eff: VisualizerEffect) {
        val dataLength = waveform.size
        if (dataLength == 0) return
        val path = Path()
        val halfHeight = height / 2f
        val step = width.toFloat() / dataLength
        for (i in 0 until dataLength) {
            val value = waveform[i].toInt() and 0xFF
            val amplitude = (value - 128) / 128f * halfHeight * eff.amplitudeMultiplier
            val x = i * step
            val y = halfHeight + amplitude
            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        paint.strokeWidth = 4f
        paint.style = Paint.Style.STROKE
        if (gradient != null) {
            paint.shader = gradient
        } else {
            paint.color = eff.colors.firstOrNull() ?: 0xFFFFFFFF.toInt()
            paint.shader = null
        }
        canvas.drawPath(path, paint)
        // Reset style for next draw call.
        paint.style = Paint.Style.FILL
        paint.shader = null
    }
}

package com.example.discoapp.model

import android.graphics.Color

/**
 * Data class representing the configuration for a disco lighting effect. Each effect defines a
 * palette of colors, a duration for how long each color should be displayed, and flags to
 * optionally enable smooth fading transitions and strobe flashes.
 *
 * @property name A human readable name for the effect.
 * @property colors An array of ARGB colors to cycle through. Colors are expressed as
 *                  `Int`s rather than resource IDs so they can be created programmatically.
 * @property duration The duration in milliseconds that each color is shown. When fading is
 *                    enabled this value also controls the length of the cross‑fade.
 * @property fade When true, transitions between colors are animated using an [android.animation.ArgbEvaluator].
 * @property strobe When true, black frames are inserted between colors to create a strobing effect.
 */
data class DiscoEffect(
    val name: String,
    val colors: IntArray,
    val duration: Long,
    val fade: Boolean,
    val strobe: Boolean
)

/**
 * An enumeration of shapes that can be rendered by the audio visualizer. Each shape defines
 * how the raw audio data is transformed into a visual representation.
 */
enum class VisualizerShape {
    /** A bar graph with vertical bars that grow with amplitude. */
    BARS,
    /** A radial effect where lines radiate from the center based on amplitude. */
    RADIAL,
    /** A continuous waveform drawn across the width of the view. */
    WAVE
}

/**
 * Data class describing the configuration for a sound visualizer effect. It controls the
 * aesthetics of the visualization by specifying the number of bars, the shape used to
 * represent audio data, a palette of colors to draw with and a multiplier that scales the
 * amplitude values for more dramatic visuals.
 *
 * @property name A human readable name for the effect.
 * @property barCount The number of visual elements (bars or radial spokes) to draw. A larger
 *                    number produces a finer representation but may impact performance.
 * @property shape The shape used to render the audio data (bars, radial lines or waveform).
 * @property colors An array of ARGB colors used to paint the visual elements. If multiple
 *                  colors are provided they are cycled per bar. A shader can be used for
 *                  smoother gradients.
 * @property amplitudeMultiplier Scales the amplitude values captured from the audio session.
 */
data class VisualizerEffect(
    val name: String,
    val barCount: Int,
    val shape: VisualizerShape,
    val colors: IntArray,
    val amplitudeMultiplier: Float
)
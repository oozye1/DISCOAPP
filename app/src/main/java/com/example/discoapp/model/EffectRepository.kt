package com.example.discoapp.model

import android.graphics.Color
import java.util.Random

/**
 * Factory methods for generating a collection of disco and visualizer effects. To keep the
 * application lightweight and to avoid hand‑crafting dozens of variations, this repository
 * algorithmically derives different combinations of color palettes, animation speeds, shapes
 * and amplitude multipliers based on the index of the requested effect. This makes it easy
 * to provide a large number of unique visual experiences without repeating code.
 */
object EffectRepository {

    /** A set of base color palettes used to construct more complex effects. */
    private val palettes: List<IntArray> = listOf(
        intArrayOf(Color.parseColor("#ff1744"), Color.parseColor("#d500f9"), Color.parseColor("#2979ff"), Color.parseColor("#00e5ff")),
        intArrayOf(Color.parseColor("#ff6f00"), Color.parseColor("#fdd835"), Color.parseColor("#64dd17"), Color.parseColor("#1de9b6")),
        intArrayOf(Color.parseColor("#ab47bc"), Color.parseColor("#ef5350"), Color.parseColor("#ffa726"), Color.parseColor("#66bb6a")),
        intArrayOf(Color.parseColor("#00b0ff"), Color.parseColor("#651fff"), Color.parseColor("#c51162"), Color.parseColor("#ff4081")),
        intArrayOf(Color.parseColor("#4caf50"), Color.parseColor("#009688"), Color.parseColor("#00bcd4"), Color.parseColor("#3f51b5")),
        intArrayOf(Color.parseColor("#ff8a80"), Color.parseColor("#ff80ab"), Color.parseColor("#ea80fc"), Color.parseColor("#b388ff")),
        intArrayOf(Color.parseColor("#ffd180"), Color.parseColor("#ffe57f"), Color.parseColor("#dcedc8"), Color.parseColor("#b2ebf2")),
        intArrayOf(Color.parseColor("#81d4fa"), Color.parseColor("#80cbc4"), Color.parseColor("#c5e1a5"), Color.parseColor("#f8bbd0"))
    )

    /**
     * Create a list of disco effects. Each effect's parameters are derived from its index
     * to produce a variety of rhythms, fades and strobing behaviours. The number of effects
     * generated is determined by [count].
     */
    fun createDiscoEffects(count: Int): List<DiscoEffect> {
        val effects = mutableListOf<DiscoEffect>()
        val random = Random(System.currentTimeMillis())
        for (i in 0 until count) {
            val name = "Effect ${i + 1}"
            val duration = (250 + random.nextInt(750)).toLong()
            val fade = random.nextBoolean()
            val strobe = random.nextBoolean()
            val palette = createPalette(random)
            effects += DiscoEffect(name, palette, duration, fade, strobe)
        }
        return effects
    }

    /**
     * Create a list of visualizer effects. Each effect's attributes are derived from its index
     * to vary bar counts, shapes, colors and amplitude scaling. The number of effects generated
     * is determined by [count].
     */
    fun createVisualizerEffects(count: Int): List<VisualizerEffect> {
        val effects = mutableListOf<VisualizerEffect>()
        for (i in 0 until count) {
            val palette = palettes[(i + 2) % palettes.size]
            // Determine bar count: 16, 32, 48, 64 depending on index.
            val barCount = when (i % 4) {
                0 -> 16
                1 -> 32
                2 -> 48
                else -> 64
            }
            // Alternate shape between bars, radial and wave.
            val shape = when (i % 3) {
                0 -> VisualizerShape.BARS
                1 -> VisualizerShape.RADIAL
                else -> VisualizerShape.WAVE
            }
            // Determine amplitude multiplier between 2.0f and 5.0f.
            val amplitudeMultiplier = 2.0f + (i % 4) * 0.75f
            val name = "Visualizer #${i + 1}"
            effects += VisualizerEffect(name, barCount, shape, palette, amplitudeMultiplier)
        }
        return effects
    }

    private fun createPalette(random: Random): IntArray {
        return palettes[random.nextInt(palettes.size)]
    }
}

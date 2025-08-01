package com.example.discoapp

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import com.example.discoapp.model.DiscoEffect

/**
 * A custom view responsible for rendering a full‑screen disco lighting effect. The view cycles
 * through a list of colors defined in [DiscoEffect.colors] at a specified interval. When
 * [DiscoEffect.fade] is enabled, the transition between colors is animated using an
 * [ArgbEvaluator]; otherwise colors switch abruptly. If strobing is enabled, black frames
 * are inserted between colors to emulate a traditional strobe light.
 */
class DiscoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /** A handler tied to the main thread for scheduling color changes when not animating. */
    private val handler = Handler(Looper.getMainLooper())
    private var effect: DiscoEffect? = null
    private var currentColorIndex: Int = 0
    private var animator: ValueAnimator? = null
    private var currentBackgroundColor: Int = Color.BLACK

    /** Set a new disco lighting effect. This restarts the color cycle from the beginning. */
    fun setEffect(effect: DiscoEffect) {
        // Cancel any running animation or delayed callbacks.
        animator?.cancel()
        handler.removeCallbacksAndMessages(null)
        // Apply the effect.
        this.effect = effect
        currentColorIndex = 0
        // Kick off the first frame.
        startNext()
    }

    /** Advance to the next color in the effect list, animating if necessary. */
    private fun startNext() {
        val eff = effect ?: return
        var colors = eff.colors
        // If strobe is enabled, interleave black frames between colors for a strobing effect.
        if (eff.strobe) {
            val strobeList = mutableListOf<Int>()
            for (c in colors) {
                strobeList.add(c)
                strobeList.add(android.graphics.Color.BLACK)
            }
            colors = strobeList.toIntArray()
        }
        if (colors.isEmpty()) return
        val nextColor = colors[currentColorIndex]
        if (eff.fade) {
            animator = android.animation.ValueAnimator.ofObject(android.animation.ArgbEvaluator(), currentBackgroundColor, nextColor).apply {
                duration = eff.duration
                addUpdateListener { animation ->
                    val color = animation.animatedValue as Int
                    setBackgroundColor(color)
                    currentBackgroundColor = color
                }
                doOnEnd {
                    currentColorIndex = (currentColorIndex + 1) % colors.size
                    startNext()
                }
                start()
            }
        } else {
            setBackgroundColor(nextColor)
            currentBackgroundColor = nextColor
            handler.postDelayed({
                currentColorIndex = (currentColorIndex + 1) % colors.size
                startNext()
            }, eff.duration)
        }
    }
}

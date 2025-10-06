package com.keagan.complete.ui.splash

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.keagan.complete.R
import kotlin.math.min

/**
 * Pastel swoosh -> writes "PlanDemic" -> writes tagline.
 * Starts automatically when attached.
 */
class SwooshWriterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Text to draw (defaults to your strings)
    private val brand = resources.getString(R.string.app_brand)          // "PlanDemic"
    private val tagline = resources.getString(R.string.app_tagline)      // "Organize • Plan • Complete"

    // Colors (pastel pink/blue + onSurface)
    private val pink = ContextCompat.getColor(context, R.color.primary)
    private val blue = ContextCompat.getColor(context, R.color.secondary)
    private val onSurface = ContextCompat.getColor(context, R.color.onSurface)

    // Swoosh path + paint
    private val swooshPath = Path()
    private val swooshPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        strokeWidth = dp(6f)
    }
    private var swooshProgress = 0f // 0..1

    // Text paints
    private val brandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = onSurface
        textAlign = Paint.Align.CENTER
    }
    private val tagPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = onSurface
        textAlign = Paint.Align.CENTER
    }

    // Reveal fractions for text
    private var brandReveal = 0f // 0..1
    private var tagReveal = 0f   // 0..1

    // Layout metrics
    private var brandBaseline = 0f
    private var tagBaseline = 0f
    private var contentLeft = 0f
    private var contentRight = 0f

    private var started = false

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!started) {
            started = true
            // Defer until we have size to build gradients/paths.
            post { startAnimation() }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Set text sizes relative to card width
        brandPaint.textSize = min(w, h) * 0.12f
        tagPaint.textSize = min(w, h) * 0.045f

        // Baselines
        brandBaseline = h * 0.52f
        tagBaseline = h * 0.68f

        // Content bounds
        val pad = dp(20f)
        contentLeft = pad
        contentRight = w - pad

        // Build a nice bezier swoosh across the card
        buildSwooshPath(w, h)

        // Pastel gradient for the swoosh
        swooshPaint.shader = LinearGradient(
            0f, 0f, w.toFloat(), 0f,
            intArrayOf(blue, pink),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
    }

    private fun buildSwooshPath(w: Int, h: Int) {
        swooshPath.reset()
        val startX = contentLeft
        val endX = contentRight
        val midX = (startX + endX) / 2f
        val y = h * 0.38f
        val up = y - dp(24f)
        val down = y + dp(24f)

        // gentle S-curve
        swooshPath.moveTo(startX, y)
        swooshPath.cubicTo(
            startX + dp(40f), up,
            midX - dp(30f), down,
            midX, y
        )
        swooshPath.cubicTo(
            midX + dp(30f), up,
            endX - dp(40f), down,
            endX, y
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw swoosh with partial length using PathMeasure + DashPathEffect trick
        val measure = PathMeasure(swooshPath, false)
        val length = measure.length
        swooshPaint.pathEffect = DashPathEffect(floatArrayOf(length, length), (1f - swooshProgress) * length)
        canvas.drawPath(swooshPath, swooshPaint)

        // Draw brand with reveal by clip
        drawRevealedText(canvas, brandPaint, brand, brandBaseline, brandReveal)

        // Draw tagline with reveal by clip
        drawRevealedText(canvas, tagPaint, tagline, tagBaseline, tagReveal)
    }

    private fun drawRevealedText(canvas: Canvas, paint: Paint, text: String, baseline: Float, reveal: Float) {
        if (reveal <= 0f) return
        val cx = width / 2f
        // Gradient ink that matches swoosh (subtle)
        paint.shader = LinearGradient(
            cx - width / 2f, 0f, cx + width / 2f, 0f,
            intArrayOf(blue, onSurface, pink),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )

        val save = canvas.save()
        val totalWidth = paint.measureText(text)
        val half = totalWidth / 2f
        // Reveal from left to right
        val left = cx - half
        val right = left + totalWidth * reveal
        canvas.clipRect(left, 0f, right, height.toFloat())
        canvas.drawText(text, cx, baseline, paint)
        canvas.restoreToCount(save)
    }

    fun startAnimation(
        swooshDuration: Long = 1100L,
        brandDuration: Long = 1100L,
        tagDuration: Long = 1100L,
        delayBetween: Long = 1100L
    ) {
        // Swoosh
        val a1 = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = swooshDuration
            interpolator = PathInterpolatorCompat.easeOut()
            addUpdateListener {
                swooshProgress = it.animatedValue as Float
                invalidate()
            }
        }

        // Brand write
        val a2 = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = brandDuration
            startDelay = delayBetween
            interpolator = PathInterpolatorCompat.easeInOut()
            addUpdateListener {
                brandReveal = it.animatedValue as Float
                invalidate()
            }
        }

        // Tagline write
        val a3 = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = tagDuration
            startDelay = delayBetween
            interpolator = PathInterpolatorCompat.easeInOut()
            addUpdateListener {
                tagReveal = it.animatedValue as Float
                invalidate()
            }
        }

        AnimatorSet().apply {
            playSequentially(a1, a2, a3)
            start()
        }
    }

    private fun dp(v: Float) = v * resources.displayMetrics.density

    /**
     * Small helpers for nicer timing without adding dependencies.
     */
    private object PathInterpolatorCompat {
        fun easeOut() = android.view.animation.AccelerateDecelerateInterpolator()
        fun easeInOut() = android.view.animation.DecelerateInterpolator()
    }
}

package com.keagan.complete.ui.splash

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.keagan.complete.R
import kotlin.math.min

class SwooshView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var primary = ContextCompat.getColor(context, R.color.primary)
    private var secondary = ContextCompat.getColor(context, R.color.secondary)
    private var durationMs = 1200

    private val paint1 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val paint2 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val arcRect = RectF()
    private var sweepProgress = 0f // 0..1
    private var animator: ValueAnimator? = null

    init {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.SwooshView)
            try {
                primary = ta.getColor(R.styleable.SwooshView_sw_primaryColor, primary)
                secondary = ta.getColor(R.styleable.SwooshView_sw_secondaryColor, secondary)
                durationMs = ta.getInteger(R.styleable.SwooshView_sw_duration, durationMs)
            } finally {
                ta.recycle()
            }
        }
        paint1.color = primary
        paint2.color = secondary
        // Strokes sized relative to display density; adjusted in onSizeChanged too
        val baseStroke = resources.displayMetrics.density * 12
        paint1.strokeWidth = baseStroke
        paint2.strokeWidth = baseStroke * 0.66f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val size = min(w, h) * 0.9f
        val cx = w / 2f
        val cy = h / 2f
        arcRect.set(cx - size / 2, cy - size / 2, cx + size / 2, cy + size / 2)

        // Keep stroke width looking nice on larger screens
        val base = min(w, h) / 24f
        paint1.strokeWidth = base
        paint2.strokeWidth = base * 0.66f
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnim()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }

    private fun startAnim() {
        animator?.cancel()
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = durationMs.toLong()
            addUpdateListener {
                sweepProgress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Two pastel arcs that sweep in as progress grows
        val start1 = -60f
        val sweep1 = 240f * sweepProgress
        canvas.drawArc(arcRect, start1, sweep1, false, paint1)

        val start2 = 140f
        val sweep2 = 240f * sweepProgress
        canvas.drawArc(arcRect, start2, sweep2, false, paint2)
    }
}

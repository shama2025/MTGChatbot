package com.mashaffer.mymtgchatbot
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View


class GradientBackgroundView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val gradientPaint = Paint()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val shader = LinearGradient(
            0f, 0f,
            width.toFloat(), height.toFloat(),
            intArrayOf(
                Color.WHITE,
                Color.BLUE,
                Color.BLACK,
                Color.RED,
                Color.GREEN
            ),
            floatArrayOf(0f, 0.25f, 0.5f, 0.75f, 1f),
            Shader.TileMode.CLAMP
        )

        gradientPaint.shader = shader
        gradientPaint.alpha = (0.25f * 255).toInt()

        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), gradientPaint)
    }
}

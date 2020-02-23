package ru.skillbranch.skillarticles.markdown.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.style.LeadingMarginSpan
import androidx.annotation.ColorInt
import androidx.annotation.Px

class BlockquotesSpan (
    @Px
    private val gapWith: Float,
    @Px
    private val quoteWith: Float,
    @ColorInt
    private val lineColor: Int
) : LeadingMarginSpan {
    override fun drawLeadingMargin(
        canvas: Canvas,
        paint: Paint,
        currentMigrationLocation: Int,
        paragraphDirection: Int,
        lineTop: Int,
        lineBaseline: Int,
        lineBottom: Int,
        text: CharSequence?,
        lineStart: Int,
        lineEnd: Int,
        isFirstLine: Boolean,
        layout: Layout?
    ) {
        paint.withCustomColor {
            canvas.drawLine(
                quoteWith/2f,
                lineTop.toFloat(),
                quoteWith/2f,
                lineBottom.toFloat(),
                paint
            )
        }
    }

    override fun getLeadingMargin(first: Boolean): Int {
        return (quoteWith + gapWith).toInt()
    }

    private inline fun Paint.withCustomColor(block: () -> Unit) {
        val oldColor = color
        val oldStyle = style
        val oldWidth = strokeWidth

        color = lineColor
        style = Paint.Style.STROKE
        strokeWidth = quoteWith

        block()

        color = oldColor
        style = oldStyle
        strokeWidth = oldWidth

    }
}
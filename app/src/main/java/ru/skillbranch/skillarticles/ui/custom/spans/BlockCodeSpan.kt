package ru.skillbranch.skillarticles.ui.custom.spans

import android.graphics.*
import android.text.style.ReplacementSpan
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting
import ru.skillbranch.skillarticles.data.repositories.Element

class BlockCodeSpan(
    @ColorInt
    private val textColor: Int,
    @ColorInt
    private val bgColor: Int,
    @Px
    private val cornerRadius: Float,
    @Px
    private val padding: Float,
    private val type: Element.BlockCode.Type
) : ReplacementSpan() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var rect = RectF()
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var path = Path()

//    private var fm: Paint.FontMetricsInt? = null
//    private var originAscent = 0
//    private var originDescent = 0

    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        fm?.let {
            //            this.fm = fm
//            originAscent = fm.ascent
//            originDescent = fm.descent
            when (type) {
                Element.BlockCode.Type.SINGLE -> {
                    fm.ascent = (paint.ascent() - 2 * padding).toInt()
                    fm.descent = (paint.descent() + 2 * padding).toInt()
                }
                Element.BlockCode.Type.START -> {
                    fm.ascent = (paint.ascent() - 2 * padding).toInt()
                    fm.descent = (paint.descent()).toInt()
                }
                Element.BlockCode.Type.MIDDLE -> {
                    fm.ascent = (paint.ascent()).toInt()
                    fm.descent = (paint.descent()).toInt()
                }
                Element.BlockCode.Type.END -> {
                    fm.ascent = (paint.ascent()).toInt()
                    fm.descent = (paint.descent() + 2 * padding).toInt()
                }
            }
        }
        return 0
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {

        when (type) {
            Element.BlockCode.Type.SINGLE -> {
                paint.forBackground {
                    rect.set(0f, top.toFloat(), canvas.width.toFloat(), bottom.toFloat())
                    canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
                }
            }
            Element.BlockCode.Type.START -> {
                paint.forBackground {
                    path.reset()
                    path.addRoundRect(
                        RectF(
                            0f,
                            top.toFloat() + padding,
                            canvas.width.toFloat(),
                            bottom.toFloat()
                        ),
                        floatArrayOf(
                            cornerRadius,
                            cornerRadius,
                            cornerRadius,
                            cornerRadius,
                            0f,
                            0f,
                            0f,
                            0f
                        ),
                        Path.Direction.CW
                    )
                    canvas.drawPath(path, paint)
                }
            }
            Element.BlockCode.Type.END -> {
                paint.forBackground {
                    path.reset()
                    path.addRoundRect(
                        RectF(
                            0f,
                            top.toFloat(),
                            canvas.width.toFloat(),
                            bottom.toFloat() - padding
                        ),
                        floatArrayOf(
                            0f,
                            0f,
                            0f,
                            0f,
                            cornerRadius,
                            cornerRadius,
                            cornerRadius,
                            cornerRadius
                        ),
                        Path.Direction.CW
                    )
                    canvas.drawPath(path, paint)
                }
            }
            Element.BlockCode.Type.MIDDLE -> {
                paint.forBackground {
                    path.reset()
                    path.addRoundRect(
                        RectF(
                            0f,
                            top.toFloat(),
                            canvas.width.toFloat(),
                            bottom.toFloat()
                        ),
                        floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f),
                        Path.Direction.CW
                    )
                    canvas.drawPath(path, paint)
                }
            }

        }

        paint.forText {
            canvas.drawText(text ?: "", start, end, x + padding, y.toFloat(), paint)
        }


//        fm?.let {
//            //if (type == Element.BlockCode.Type.SINGLE || type == Element.BlockCode.Type.END)
//            it.ascent = originAscent
//            it.descent = originDescent
//        }
//        fm = null
    }

    private inline fun Paint.forText(block: () -> Unit) {
        val oldSize = textSize
        val oldStyle = typeface?.style ?: 0
        val oldFont = typeface
        val oldColor = color

        color = textColor
        typeface = Typeface.create(Typeface.MONOSPACE, oldStyle)
        textSize *= 0.85f

        block()

        textSize = oldSize
        typeface = oldFont
        color = oldColor
    }

    private inline fun Paint.forBackground(block: () -> Unit) {
        val oldColor = color
        val oldStyle = style
        color = bgColor
        style = Paint.Style.FILL

        block()

        color = oldColor
        style = oldStyle
    }

}
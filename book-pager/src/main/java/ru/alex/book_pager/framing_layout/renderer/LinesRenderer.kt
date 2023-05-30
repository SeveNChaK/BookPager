package ru.alex.book_pager.framing_layout.renderer

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import ru.alex.book_pager.dpToPixelSize
import ru.alex.book_pager.dpToPixels

class LinesRenderer(frameType: FrameType.Stroke) : BaseFrameRenderer(hasShadow = frameType.shadow) {

    private val radius = frameType.radiusDP.dpToPixels()
    private val strokeWidthPx = frameType.widthDP.dpToPixelSize()

    private val strokeHalfWidthPx = strokeWidthPx / 2

    private val paint = Paint().apply {
        color = Color.parseColor(frameType.color)
        style = Paint.Style.STROKE
        strokeWidth = strokeWidthPx.toFloat()
    }
    private val bufContentPath = Path()

    override fun getOutline(contentRect: RectF, viewWidth: Int, viewHeight: Int, outline: Outline) {
        outline.setRoundRect(
            0,
            0,
            viewWidth,
            viewHeight,
            radius * 1.5f
        )
    }

    override fun draw(canvas: Canvas, viewWidth: Int, viewHeight: Int, contentRect: RectF, drawContent: () -> Unit) {
        bufContentPath.reset()
        bufContentPath.addRoundRect(
            contentRect.left,
            contentRect.top,
            contentRect.right,
            contentRect.bottom,
            radius,
            radius,
            Path.Direction.CCW
        )

        canvas.apply {
            save()
            clipPath(bufContentPath)
            drawContent()
            restore()

            drawPath(bufContentPath, paint)
        }
    }

    override fun getLeftOffset() = strokeHalfWidthPx

    override fun getTopOffset() = strokeHalfWidthPx

    override fun getRightOffset() = strokeHalfWidthPx

    override fun getBottomOffset() = strokeHalfWidthPx
}

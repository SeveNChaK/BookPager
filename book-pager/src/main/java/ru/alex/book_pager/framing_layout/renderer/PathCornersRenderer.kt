package ru.alex.book_pager.framing_layout.renderer

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import kotlin.math.roundToInt
import ru.alex.book_pager.dpToPixelSize
import ru.alex.book_pager.dpToPixels

class PathCornersRenderer(
    private val frameType: FrameType.PathCorners,
    shadow: Boolean = false,
) : BaseFrameRenderer(
    offsetRect = frameType.offsetDP.let { Rect(it, it, it, it) },
    hasShadow = shadow
) {

    private val paint = Paint().apply {
        color = Color.parseColor(frameType.color)
        strokeWidth = 3.dpToPixels()
        style = Paint.Style.FILL_AND_STROKE
    }
    private val cornerSize = frameType.cornerSizeDP.dpToPixelSize()
    private val cornerBounds = Rect(0, 0, cornerSize, cornerSize)

    override fun getOutline(contentRect: RectF, viewWidth: Int, viewHeight: Int, outline: Outline) {
        outline.setRect(
            contentRect.left.roundToInt(),
            contentRect.top.roundToInt(),
            contentRect.right.roundToInt(),
            contentRect.bottom.roundToInt()
        )
    }

    override fun draw(canvas: Canvas, viewWidth: Int, viewHeight: Int, contentRect: RectF, drawContent: () -> Unit) {
        val width = viewWidth.toFloat()
        val height = viewHeight.toFloat()

        canvas.apply {
            save()
            clipRect(contentRect)
            drawContent()
            restore()
        }

        drawCorner(canvas, rotation = 0f)
        drawCorner(canvas, translationY = cornerSize - width, rotation = 90f)
        drawCorner(canvas, cornerSize - width, cornerSize - height, rotation = 180f)
        drawCorner(canvas, translationX = cornerSize - height, rotation = 270f)
    }

    private fun drawCorner(
        canvas: Canvas,
        translationX: Float = 0f,
        translationY: Float = 0f,
        rotation: Float = 0f
    ) {
        canvas.save()
        canvas.rotate(rotation, cornerBounds.centerX().toFloat(), cornerBounds.centerY().toFloat())
        canvas.translate(translationX, translationY)
        canvas.drawPath(frameType.path, paint)
        canvas.restore()
    }
}

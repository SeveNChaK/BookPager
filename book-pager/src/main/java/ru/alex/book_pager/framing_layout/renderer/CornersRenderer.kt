package ru.alex.book_pager.framing_layout.renderer

import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import kotlin.math.roundToInt
import ru.alex.book_pager.ApplicationContextProvider
import ru.alex.book_pager.dpToPixelSize

class CornersRenderer(
    frameType: FrameType.Corner,
) : BaseFrameRenderer(
    offsetRect = frameType.offsetDP.dpToPixelSize().let { Rect(it, it, it, it) },
    hasShadow = frameType.shadow
) {

    private val cornerSize = frameType.cornerSizeDP.dpToPixelSize()
    private val cornerBounds = Rect(0, 0, cornerSize, cornerSize)

    private val cornerDrawable = ContextCompat.getDrawable(
        ApplicationContextProvider.applicationContext,
        frameType.cornerRes
    )!!

    init {
        cornerDrawable.bounds = cornerBounds
    }

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

        cornerDrawable.let {
            drawCorner(canvas, it, rotation = 0f)
            drawCorner(canvas, it, translationY = cornerSize - width, rotation = 90f)
            drawCorner(canvas, it, cornerSize - width, cornerSize - height, rotation = 180f)
            drawCorner(canvas, it, translationX = cornerSize - height, rotation = 270f)
        }
    }

    private fun drawCorner(
        canvas: Canvas,
        drawable: Drawable,
        translationX: Float = 0f,
        translationY: Float = 0f,
        rotation: Float = 0f
    ) {
        canvas.save()
        canvas.rotate(rotation, cornerBounds.centerX().toFloat(), cornerBounds.centerY().toFloat())
        canvas.translate(translationX, translationY)
        drawable.draw(canvas)
        canvas.restore()
    }
}

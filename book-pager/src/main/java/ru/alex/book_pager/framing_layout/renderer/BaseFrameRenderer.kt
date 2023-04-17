package ru.alex.book_pager.framing_layout.renderer

import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Rect
import android.graphics.RectF
import ru.alex.book_pager.dpToPixelSize

abstract class BaseFrameRenderer(
    protected val offsetRect: Rect = Rect(0, 0, 0, 0),
    protected val hasShadow: Boolean = false
) {
    private val frameLayerElevation = if (hasShadow) {
        2.dpToPixelSize()
    } else 0

    open fun getLeftOffset() = offsetRect.left

    open fun getTopOffset() = offsetRect.top

    open fun getRightOffset() = offsetRect.right

    open fun getBottomOffset() = offsetRect.bottom

    fun getElevation() = frameLayerElevation.toFloat()

    abstract fun getOutline(contentRect: RectF, viewWidth: Int, viewHeight: Int, outline: Outline)
    abstract fun draw(canvas: Canvas, viewWidth: Int, viewHeight: Int, contentRect: RectF, drawContent: () -> Unit)
}

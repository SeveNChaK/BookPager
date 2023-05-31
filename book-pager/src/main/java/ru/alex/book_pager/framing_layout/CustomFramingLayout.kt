package ru.alex.book_pager.framing_layout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import kotlin.math.min
import ru.alex.book_pager.framing_layout.renderer.BaseFrameRenderer

/**
 * Лейаут на основе FrameLayout, который вокруг своего контента рисует дополнительно оформление.
 * Оформление задается с помощью BaseFrameRenderer.
 * @see ru.alex.book_pager.framing_layout.renderer.BaseFrameRenderer
 */
class CustomFramingLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {

    private val contentRect = RectF()

    private var frameRenderer: BaseFrameRenderer? = null
    var maxWidth = -1

    init {
        setBackgroundColor(Color.TRANSPARENT)
        clipToPadding = false
        clipChildren = false
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                frameRenderer?.getOutline(contentRect, width, height, outline)
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val contentLeft = paddingLeft.toFloat()
        val contentTop = paddingTop.toFloat()
        val contentRight = ((right - left) - paddingRight).toFloat()
        val contentBottom = ((bottom - top) - paddingBottom).toFloat()
        contentRect.set(contentLeft, contentTop, contentRight, contentBottom)
        invalidateOutline()
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun draw(canvas: Canvas) {
        frameRenderer?.draw(canvas, width, height, contentRect) {
            super.draw(canvas)
        } ?: super.draw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val targetWidthMeasureSpec = if (maxWidth < 0) {
            widthMeasureSpec
        } else {
            val mode = MeasureSpec.getMode(widthMeasureSpec)
            if (mode == MeasureSpec.UNSPECIFIED) {
                MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST)
            } else {
                MeasureSpec.makeMeasureSpec(min(maxWidth, MeasureSpec.getSize(widthMeasureSpec)), mode)
            }
        }

        super.onMeasure(targetWidthMeasureSpec, heightMeasureSpec)
    }

    fun setupRenderer(renderer: BaseFrameRenderer) {
        this.frameRenderer = renderer
        elevation = frameRenderer?.getElevation() ?: 0f
        setPadding(
            renderer.getLeftOffset(),
            renderer.getTopOffset(),
            renderer.getRightOffset(),
            renderer.getBottomOffset()
        )
    }
}

package ru.alex.book_pager.curl_effect.graphics

import android.graphics.PointF

class CurveBezier {
    val start = PointF(0f, 0f)
    val base = PointF(0f, 0f)
    val end = PointF(0f, 0f)

    fun set(curveBezier: CurveBezier) {
        start.set(curveBezier.start)
        base.set(curveBezier.base)
        end.set(curveBezier.end)
    }

    fun offset(dx: Float, dy: Float) {
        start.offset(dx, dy)
        base.offset(dx, dy)
        end.offset(dx, dy)
    }

    fun toStandard(viewWidth: Float, viewHeight: Float, isTopCorner: Boolean, isTopBend: Boolean) {
        start.toStandard(viewWidth, viewHeight, isTopCorner, isTopBend)
        base.toStandard(viewWidth, viewHeight, isTopCorner, isTopBend)
        end.toStandard(viewWidth, viewHeight, isTopCorner, isTopBend)
    }

    fun fromStandard(viewWidth: Float, viewHeight: Float, isTopCorner: Boolean, isTopBend: Boolean) {
        start.fromStandard(viewWidth, viewHeight, isTopCorner, isTopBend)
        base.fromStandard(viewWidth, viewHeight, isTopCorner, isTopBend)
        end.fromStandard(viewWidth, viewHeight, isTopCorner, isTopBend)
    }

    override fun toString(): String {
        return "${javaClass.simpleName}{$start; $base; $end}"
    }
}
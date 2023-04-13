package ru.alex.book_pager.curl_effect.graphics

import android.graphics.PointF

class Circle {
    val center = PointF(0f, 0f)
    var radius = 0f

    fun set(circle: Circle) {
        center.set(circle.center)
        radius = circle.radius
    }

    fun offset(dx: Float, dy: Float) {
        center.offset(dx, dy)
    }

    fun toStandard(viewWidth: Float, viewHeight: Float, isTopCorner: Boolean, isTopBend: Boolean) {
        center.toStandard(viewWidth, viewHeight, isTopCorner, isTopBend)
    }

    fun fromStandard(viewWidth: Float, viewHeight: Float, isTopCorner: Boolean, isTopBend: Boolean) {
        center.fromStandard(viewWidth, viewHeight, isTopCorner, isTopBend)
    }
}
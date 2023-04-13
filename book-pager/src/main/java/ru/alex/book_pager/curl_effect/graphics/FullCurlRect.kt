package ru.alex.book_pager.curl_effect.graphics

import android.graphics.PointF

internal class FullCurlRect {

    var isTopCornerMoving = false

    val leftBottom = PointF(0f, 0f) //уголок, который двигается снизу
    val leftTop = PointF(0f, 0f) //уголок, который двигается сверху
    val rightBottom = PointF(0f, 0f) //место изгиба страницы снизу
    val rightTop = PointF(0f, 0f) //место изгиба страницы сверху

    internal fun set(other: FullCurlRect) {
        isTopCornerMoving = other.isTopCornerMoving

        leftBottom.set(other.leftBottom)
        leftTop.set(other.leftTop)
        rightBottom.set(other.rightBottom)
        rightTop.set(other.rightTop)
    }

    internal fun reset() {
        isTopCornerMoving = false

        leftBottom.set(0f, 0f)
        rightBottom.set(0f, 0f)
        leftTop.set(0f, 0f)
        rightTop.set(0f, 0f)
    }
}

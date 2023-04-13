package ru.alex.book_pager.curl_effect.graphics

import android.graphics.PointF

class PartCurl {
    val corner = PointF(0f, 0f)
    val curve = CurveBezier()
    val closerCurve = CurveBezier()

    var isCut = false
    val simpleLeft = PointF(0f, 0f)
    val simpleRight = PointF(0f, 0f)

    val debugCircle = Circle()

    fun reset() {
        corner.set(0f, 0f)

        curve.start.set(0f, 0f)
        curve.base.set(0f, 0f)
        curve.end.set(0f, 0f)

        closerCurve.start.set(0f, 0f)
        closerCurve.base.set(0f, 0f)
        closerCurve.end.set(0f, 0f)

        isCut = false
        simpleLeft.set(0f, 0f)
        simpleRight.set(0f, 0f)
    }

    fun set(partCurl: PartCurl) {
        corner.set(partCurl.corner)
        curve.set(partCurl.curve)
        closerCurve.set(partCurl.closerCurve)

        isCut = partCurl.isCut
        simpleRight.set(partCurl.simpleRight)
        simpleLeft.set(partCurl.simpleLeft)

        debugCircle.set(partCurl.debugCircle)
    }

    fun offset(dx: Float, dy: Float) {
        corner.offset(dx, dy)
        curve.offset(dx, dy)
        closerCurve.offset(dx, dy)

        simpleRight.offset(dx, dy)
        simpleLeft.offset(dx, dy)

        debugCircle.offset(dx, dy)
    }

    fun toStandard(viewWidth: Float, viewHeight: Float, isTopCorner: Boolean, isTopBend: Boolean) {
        corner.toStandard(viewWidth, viewHeight, isTopCorner, isTopBend)
        curve.toStandard(viewWidth, viewHeight, isTopCorner, isTopBend)
        closerCurve.toStandard(viewWidth, viewHeight, isTopCorner, isTopBend)

        simpleRight.toStandard(viewWidth, viewHeight, isTopCorner, isTopBend)
        simpleLeft.toStandard(viewWidth, viewHeight, isTopCorner, isTopBend)

        debugCircle.toStandard(viewWidth, viewHeight, isTopCorner, isTopBend)
    }

    fun fromStandard(viewWidth: Float, viewHeight: Float, isTopCorner: Boolean, isTopBend: Boolean) {
        corner.fromStandard(viewWidth, viewHeight, isTopCorner, isTopBend)
        curve.fromStandard(viewWidth, viewHeight, isTopCorner, isTopBend)
        closerCurve.fromStandard(viewWidth, viewHeight, isTopCorner, isTopBend)

        simpleRight.fromStandard(viewWidth, viewHeight, isTopCorner, isTopBend)
        simpleLeft.fromStandard(viewWidth, viewHeight, isTopCorner, isTopBend)

        debugCircle.fromStandard(viewWidth, viewHeight, isTopCorner, isTopBend)
    }

    override fun toString(): String {
        return "${javaClass.simpleName}{$corner; $curve; isCut $isCut; cutL $simpleLeft; cutR $simpleRight}"
    }
}
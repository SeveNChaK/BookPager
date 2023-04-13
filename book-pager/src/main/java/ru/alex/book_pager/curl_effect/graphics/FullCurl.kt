package ru.alex.book_pager.curl_effect.graphics

import android.graphics.Path

internal class FullCurl {

    private val bufNextPagePath = Path()
    private val bufCurlPath = Path()

    val topCurl = PartCurl()
    val bottomCurl = PartCurl()

    val background = CurlBackgroundTransformation()

    internal fun reset() {
        topCurl.reset()
        bottomCurl.reset()

        //todo shadow and background
    }

    internal fun set(other: FullCurl) {
        topCurl.set(other.topCurl)
        bottomCurl.set(other.bottomCurl)
        background.set(other.background)
    }

    internal fun getPath(isTopCorner: Boolean) = if (isTopCorner) {
        getPathWhenTopCorner()
    } else {
        getPathWhenBottomCorner()
    }

    internal fun getNextPageMask(isTopCorner: Boolean, viewWidth: Float, viewHeight: Float): Path {
        bufNextPagePath.rewind()
        if (isTopCorner) {
            if (bottomCurl.isCut) {
                bufNextPagePath.apply {
                    moveTo(bottomCurl.simpleRight)
                    lineTo(topCurl.closerCurve.start)
                    quadTo(topCurl.closerCurve.base, topCurl.closerCurve.end)
                    lineTo(viewWidth, 0f)
                    lineTo(viewWidth, viewHeight)
                    lineTo(bottomCurl.simpleRight)
                }
            } else {
                bufNextPagePath.apply {
                    moveTo(bottomCurl.closerCurve.end)
                    quadTo(bottomCurl.closerCurve.base, bottomCurl.closerCurve.start)
                    lineTo(topCurl.closerCurve.start)
                    quadTo(topCurl.closerCurve.base, topCurl.closerCurve.end)
                    lineTo(viewWidth, 0f)
                    lineTo(bottomCurl.curve.start)
                }
            }
        } else {
            if (topCurl.isCut) {
                bufNextPagePath.apply {
                    moveTo(bottomCurl.curve.start)
                    quadTo(bottomCurl.closerCurve.base, bottomCurl.closerCurve.start)
                    lineTo(topCurl.simpleRight)
                    lineTo(viewWidth, 0f)
                    lineTo(viewWidth, viewHeight)
                    lineTo(bottomCurl.curve.start)
                }
            } else {
                bufNextPagePath.apply {
                    moveTo(bottomCurl.curve.start)
                    quadTo(bottomCurl.closerCurve.base, bottomCurl.closerCurve.start)
                    lineTo(topCurl.closerCurve.start)
                    quadTo(topCurl.closerCurve.base, topCurl.closerCurve.end)
                    lineTo(viewWidth, viewHeight)
                    lineTo(bottomCurl.curve.start)
                }
            }
        }

        return bufNextPagePath
    }

    //Это случай, когда виден верхний край
    private fun getPathWhenTopCorner() = bufCurlPath.apply {
        rewind()

        if (bottomCurl.isCut) {
            //Это случай, когда нижний край за границами
            //Чтоб не рисовать за границами, просто находим точки на краю экрана и рисуем часть по ним

            moveTo(bottomCurl.simpleLeft)
            lineTo(topCurl.corner)
            lineTo(topCurl.curve.end)
            quadTo(topCurl.curve.base, topCurl.curve.start)
            quadTo(topCurl.closerCurve.base, topCurl.closerCurve.start)
            lineTo(bottomCurl.simpleRight)
            lineTo(bottomCurl.simpleLeft)
        } else {
            moveTo(bottomCurl.curve.start)
            quadTo(bottomCurl.curve.base, bottomCurl.curve.end)
            lineTo(topCurl.corner)
            lineTo(topCurl.curve.end)
            quadTo(topCurl.curve.base, topCurl.curve.start)
            quadTo(topCurl.closerCurve.base, topCurl.closerCurve.start)
            lineTo(bottomCurl.closerCurve.start)
            quadTo(bottomCurl.closerCurve.base, bottomCurl.closerCurve.end)
        }
    }

    //Это случай, когда виден нижний край
    private fun getPathWhenBottomCorner() = bufCurlPath.apply {
        rewind()

        moveTo(bottomCurl.curve.start.x, bottomCurl.curve.start.y)
        quadTo(bottomCurl.curve.base.x, bottomCurl.curve.base.y, bottomCurl.curve.end.x, bottomCurl.curve.end.y)
        lineTo(bottomCurl.corner.x, bottomCurl.corner.y)

        if (topCurl.isCut) {
            //Это случай, когда верхний край за границами
            //Чтоб не рисовать за границами, просто находим точки на краю экрана и рисуем часть по ним

            lineTo(topCurl.simpleLeft)
            lineTo(topCurl.simpleRight)
        } else {
            lineTo(topCurl.curve.end.x, topCurl.curve.end.y)
            quadTo(topCurl.curve.base.x, topCurl.curve.base.y, topCurl.curve.start.x, topCurl.curve.start.y)
            quadTo(topCurl.closerCurve.base.x, topCurl.closerCurve.base.y, topCurl.closerCurve.start.x, topCurl.closerCurve.start.y)
        }

        lineTo(bottomCurl.closerCurve.start.x, bottomCurl.closerCurve.start.y)
        quadTo(bottomCurl.closerCurve.base.x, bottomCurl.closerCurve.base.y, bottomCurl.closerCurve.end.x, bottomCurl.closerCurve.end.y)
    }

    override fun toString(): String {
        return "${javaClass.simpleName}{\n" +
            "\tTop: $topCurl\n" +
            "\tBottom: $bottomCurl\n" +
            "}"
    }
}

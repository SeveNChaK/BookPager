package ru.alex.book_pager.curl_effect.graphics

import android.graphics.PointF
import androidx.annotation.FloatRange
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

object CurvesHelper {

    private const val K_CURVE = 0.5f //коэффициент радиуса скругления

    private val bufB = PointF(0f, 0f)
    private val bufCurl = PartCurl()
    private val bufLineParameters = LineParameters()
    private val bufCloserCurve = CurveBezier()

    internal fun calcCurves(fullCurlRect: FullCurlRect, viewWidth: Float, viewHeight: Float, out: FullCurl) {
        val vectorCDx: Float
        val vectorCDy: Float
        val vectorCBx: Float
        val vectorCBy: Float
        //Находим длины векторов
        if (fullCurlRect.isTopCornerMoving) {
            vectorCDx = fullCurlRect.rightTop.x
            vectorCDy = 0f
            vectorCBx = fullCurlRect.rightTop.x - fullCurlRect.leftTop.x
            vectorCBy = fullCurlRect.leftTop.y
        } else {
            vectorCDx = fullCurlRect.rightBottom.x
            vectorCDy = 0f
            vectorCBx = fullCurlRect.rightBottom.x - fullCurlRect.leftBottom.x
            vectorCBy = viewHeight - fullCurlRect.leftBottom.y
        }

        //Находим угол между векторами
        val cosCurlCorner = (vectorCDx * vectorCBx + vectorCDy * vectorCBy) / sqrt(vectorCDx.pow(2) + vectorCDy.pow(2)) / sqrt(vectorCBx.pow(2) + vectorCBy.pow(2))
        val radiansCurlCorner = acos(cosCurlCorner)

        val degreesCurlCorner = Math.toDegrees(radiansCurlCorner.toDouble())

        calcCurlBackground(viewWidth, viewHeight, fullCurlRect, degreesCurlCorner.toFloat(), out)

        val calcByRect = !(degreesCurlCorner > 0f && degreesCurlCorner < 90f)
        if (fullCurlRect.isTopCornerMoving) {
            calcCurvesWhenMoveTopCorner(calcByRect, fullCurlRect, viewWidth, viewHeight, out)
        } else {
            calcCurvesWhenMoveBottomCorner(calcByRect, fullCurlRect, viewWidth, viewHeight, out)
        }
    }

    private fun calcCurlBackground(viewWidth: Float, viewHeight: Float, fullCurlRect: FullCurlRect, degreesCurlCorner: Float, out: FullCurl) {
        if (fullCurlRect.isTopCornerMoving) {
            out.background.apply {
                degreesRotate = -degreesCurlCorner
                rotatePoint.set(0f, 0f)
                translate.set(fullCurlRect.leftTop)
            }
        } else {
            out.background.apply {
                degreesRotate = degreesCurlCorner
                rotatePoint.set(0f, viewHeight)
                translate.set(fullCurlRect.leftBottom.x, -(viewHeight - fullCurlRect.leftBottom.y))
            }
        }
        out.background.scalePoint.set(viewWidth / 2f, viewHeight / 2f)
    }

    private fun calcCurvesWhenMoveTopCorner(byRect: Boolean, fullCurlRect: FullCurlRect, viewWidth: Float, viewHeight: Float, out: FullCurl) {
        //верх
        bufCurl.apply {
            corner.set(fullCurlRect.leftTop)
            curve.base.set(fullCurlRect.rightTop)

            //Приводим к нужным координатам
            toStandard(viewWidth, viewHeight, isTopCorner = true, isTopBend = true)
        }
        if (byRect) {
            calcCurveByRect(bufCurl.corner, bufCurl.curve.base, viewHeight)
        } else {
            calcCurveByTriangle(bufCurl.corner, bufCurl.curve.base, viewHeight)
        }
        //Возвращаем к исходным координатам
        bufCurl.fromStandard(viewWidth, viewHeight, isTopCorner = true, isTopBend = true)

        out.topCurl.set(bufCurl)

        //низ
        var offsetY = 0f
        if (fullCurlRect.rightBottom.y.compareTo(viewHeight) > 0) {
            /*
            Расчеты минус не поддерживают, поэтому сдвигаем, чтоб все было положительное.
            Удвоение нужно, чтобы не было нулевой координаты по Х, что приводит к нулевой
             длине вектора в расчете кривой (см. метод calcCurveBy...).
             */
            offsetY = 2f * (viewHeight - fullCurlRect.rightBottom.y)
        }
        bufCurl.apply {
            corner.set(fullCurlRect.leftTop)
            curve.base.set(fullCurlRect.rightBottom)

            //Приводим к нужным координатам
            toStandard(viewWidth, viewHeight, isTopCorner = true, isTopBend = false)
            offset(abs(offsetY), 0f)
        }
        //Низ считается наоборот
        if (byRect) {
            calcCurveByTriangle(bufCurl.corner, bufCurl.curve.base, viewHeight)
        } else {
            calcCurveByRect(bufCurl.corner, bufCurl.curve.base, viewHeight)
        }
        bufCurl.apply {
            //Возвращаем к исходным координатам
            offset(offsetY, 0f)
            fromStandard(viewWidth, viewHeight, isTopCorner = true, isTopBend = false)
        }
        out.bottomCurl.set(bufCurl)

        cutBottomIfNeeded(viewHeight, out)
    }

    private fun calcCurvesWhenMoveBottomCorner(byRect: Boolean, fullCurlRect: FullCurlRect, viewWidth: Float, viewHeight: Float, out: FullCurl) {
        //верх
        var offsetY = 0f
        if (fullCurlRect.rightTop.y.compareTo(0f) < 0) {
            /*
            Расчеты минус не поддерживают, поэтому сдвигаем, чтоб все было положительное.
            Удвоение нужно, чтобы не было нулевой координаты по Х, что приводит к нулевой
             длине вектора в расчете кривой (см. метод calcCurveBy...).
             */
            offsetY = 2f * (0f - fullCurlRect.rightTop.y)
        }
        bufCurl.apply {
            corner.set(fullCurlRect.leftBottom)
            curve.base.set(fullCurlRect.rightTop)

            //Приводим к нужным координатам
            offset(0f, offsetY)
            toStandard(viewWidth, viewHeight, isTopCorner = false, isTopBend = true)
        }
        //Верх считается наоборот
        if (byRect) {
            calcCurveByTriangle(bufCurl.corner, bufCurl.curve.base, viewHeight)
        } else {
            calcCurveByRect(bufCurl.corner, bufCurl.curve.base, viewHeight)
        }
        bufCurl.apply {
            //Возвращаем к исходным координатам
            fromStandard(viewWidth, viewHeight, isTopCorner = false, isTopBend = true)
            offset(0f, -offsetY)
        }
        out.topCurl.set(bufCurl)

        //низ
        bufCurl.apply {
            corner.set(fullCurlRect.leftBottom)
            curve.base.set(fullCurlRect.rightBottom)
        }
        if (byRect) {
            calcCurveByRect(bufCurl.corner, bufCurl.curve.base, viewHeight)
        } else {
            calcCurveByTriangle(bufCurl.corner, bufCurl.curve.base, viewHeight)
        }
        out.bottomCurl.set(bufCurl)

        cutTopIfNeeded(out)
    }

    private fun cutBottomIfNeeded(viewHeight: Float, out: FullCurl): Boolean {
        if (out.bottomCurl.curve.end.y.compareTo(viewHeight) > 0) {
            //Это случай, когда нижний край за границами
            //Чтоб не рисовать за границами, просто находим точки на краю экрана и будем рисовать этот участок по ним

            out.bottomCurl.isCut = true

            findLineParameters(out.topCurl.corner, out.bottomCurl.curve.end)
            val leftX = (viewHeight - bufLineParameters.b) / bufLineParameters.k

            findLineParameters(out.topCurl.closerCurve.start, out.bottomCurl.closerCurve.start)
            val rightX = (viewHeight - bufLineParameters.b) / bufLineParameters.k

            out.bottomCurl.simpleLeft.set(leftX, viewHeight)
            out.bottomCurl.simpleRight.set(rightX, viewHeight)
        } else {
            out.bottomCurl.isCut = false
        }

        return out.bottomCurl.isCut
    }

    private fun cutTopIfNeeded(out: FullCurl): Boolean {
        if (out.topCurl.curve.end.y.compareTo(0f) < 0) {
            //Это случай, когда верхний край за границами
            //Чтоб не рисовать за границами, просто находим точки на краю экрана и будем рисовать этот участок по ним

            out.topCurl.isCut = true

            findLineParameters(out.bottomCurl.corner, out.topCurl.curve.end)
            val leftX = (0f - bufLineParameters.b) / bufLineParameters.k

            findLineParameters(out.bottomCurl.closerCurve.start, out.topCurl.closerCurve.start)
            val rightX = (0f - bufLineParameters.b) / bufLineParameters.k

            out.topCurl.simpleLeft.set(leftX, 0f)
            out.topCurl.simpleRight.set(rightX, 0f)
        } else {
            out.topCurl.isCut = false
        }

        return out.topCurl.isCut
    }

    private fun calcCurveByRect(B: PointF, C: PointF, viewHeight: Float) {
        val EC = abs(C.x - B.x) * K_CURVE

        //Находим длины векторов
        val vectorCDx = C.x
        val vectorCDy = 0f
        val vectorCBx = C.x - B.x
        val vectorCBy = viewHeight - B.y

        //Находим угол между векторами
        val cosCornerDCB = (vectorCDx * vectorCBx + vectorCDy * vectorCBy) / sqrt(vectorCDx.pow(2) + vectorCDy.pow(2)) / sqrt(vectorCBx.pow(2) + vectorCBy.pow(2))
        val radiansCornerDCB = acos(cosCornerDCB)

        val BE = EC * tan(radiansCornerDCB)
        val BC = sqrt(BE.pow(2) + EC.pow(2))

        val degreesCornerDCB = Math.toDegrees(radiansCornerDCB.toDouble())

        val radiansCornerACB = radiansCornerDCB / 2

        val cosCornerACB = cos(radiansCornerACB)
        val AC = BC / cosCornerACB

        val sinCornerACB = sin(radiansCornerACB)
        val AB = AC * sinCornerACB

        val DC = sqrt((-1f * AC.pow(2) + (AB + BC).pow(2)) / 2f + ((AC * cosCornerACB - (AB + BC)) / 2f).pow(2)) + (AC * cosCornerACB - (AB + BC)) / 2f
        val AD = AB + BC - DC

        val halfPTriangleABC = (AB + BC + AC) / 2
        val sTriangleABC = sqrt(halfPTriangleABC * (halfPTriangleABC - AB) * (halfPTriangleABC - BC) * (halfPTriangleABC - AC))

        val halfPTriangleACD = (AC + AD + DC) / 2
        val sTriangleACD = sqrt(halfPTriangleACD * (halfPTriangleACD - AC) * (halfPTriangleACD - AD) * (halfPTriangleACD - DC))

        val sABCD = sTriangleABC + sTriangleACD
        val pABCD = AB + BC + DC + AD
        val inscribedCircleRadius = sABCD / (pABCD / 2f)

        val OC = inscribedCircleRadius / sinCornerACB
        val HC = OC * cosCornerACB

        val IC = HC

        val Ox = C.x - IC
        val Oy = viewHeight - inscribedCircleRadius

        val Ix = Ox
        val Iy = Oy + inscribedCircleRadius

        val HF: Float
        val FC: Float

        val Hx: Float
        val Hy: Float
        if (degreesCornerDCB.compareTo(90) > 0) {
            val degreesCornerBCK = 180 - degreesCornerDCB
            val radiansCornerBCK = Math.toRadians(degreesCornerBCK)
            HF = (sin(radiansCornerBCK) * HC).toFloat()
            FC = (cos(radiansCornerBCK) * HC).toFloat()
            Hx = C.x + FC
            Hy = viewHeight - HF
        } else {
            /*
            TODO Хотелось бы удалить, но нельзя, как бы странно не казалось, потому что все равно попадаем сюда.
              Воспроизвести: Потянуть уголок, а потом тянуть обратно при маленьком загибе.
              Поисследовать позже, как будет время.
             */
            HF = sin(radiansCornerDCB) * HC
            FC = cos(radiansCornerDCB) * HC
            Hx = C.x - FC
            Hy = viewHeight - HF
        }

        calcCloserCurve(Ix, Iy, C.x, C.y, Hx, Hy)

        //fill result
        bufCurl.apply {
            curve.apply {
                start.set(Ix, Iy)
                base.set(C.x, C.y)
                end.set(Hx, Hy)
            }
            closerCurve.set(bufCloserCurve)

            debugCircle.apply {
                center.set(Ox, Oy)
                radius = inscribedCircleRadius
            }
        }
    }

    private fun calcCurveByTriangle(B: PointF, C: PointF, viewHeight: Float) {
        //Находим длину векторов
        val vectorCDx = C.x
        val vectorCDy = 0f
        val vectorCBx = C.x - B.x
        val vectorCBy = viewHeight - B.y

        //Находим угол между векторами
        val cosCornerDCB = (vectorCDx * vectorCBx + vectorCDy * vectorCBy) / sqrt(vectorCDx.pow(2) + vectorCDy.pow(2)) / sqrt(vectorCBx.pow(2) + vectorCBy.pow(2))
        val radiansCornerDCB = acos(cosCornerDCB)

        /*
        Для ситуации, когда уголок выходит за границы экрана, и нужно разрешить ему туда двигаться.
        Просто берем на прямой точку с координатой x = 0 и считаем все как обычно. Данный подход не дает
        в описанной ситуации загибу снизу/сверху выйти за экран.
        Такое допустимо только для острого угла, поэтому в расчетах по четырехугольнику данное условие отсутствует.
         */
        if (B.x.compareTo(0) < 0) {
            bufB.set(
                0f,
                viewHeight - (tan(radiansCornerDCB) * C.x)
            )
            calcCurveByTriangle(bufB, C, viewHeight)
            return
        }

        val EC = abs(C.x - B.x) * K_CURVE
        val BE = EC * tan(radiansCornerDCB)
        val BC = sqrt(BE.pow(2) + EC.pow(2))

        val CG = BC / cos(radiansCornerDCB)
        val GB = CG * sin(radiansCornerDCB)

        val inscribedCircleRadius = (BC + GB - CG) / 2f
        val radiansCornerABO = Math.toRadians(45.toDouble())
        val tanCornerABO = tan(radiansCornerABO)
        val BK = inscribedCircleRadius / tanCornerABO
        val BH = BK

        val sinCornerBCE = BE / BC
        val cosCornerBCE = EC / BC
        val radiansCornerBCE = acos(cosCornerBCE)
        val HC = BC - BH
        val HF = HC * sinCornerBCE
        val FC = HC * cosCornerBCE

        val Hx = (C.x - FC).toFloat()
        val Hy = (C.y - HF).toFloat()

        val OC = HC / cos(radiansCornerBCE / 2f)
        val IC = OC * cos(radiansCornerBCE / 2f)

        val Ox = (C.x - IC).toFloat()
        val Oy = viewHeight - inscribedCircleRadius

        val Ix = Ox
        val Iy = (Oy + inscribedCircleRadius)

        calcCloserCurve(Ix, Iy, C.x, C.y, Hx, Hy)

        //fill result
        bufCurl.apply {
            curve.apply {
                start.set(Ix, Iy)
                base.set(C)
                end.set(Hx, Hy)
            }
            closerCurve.set(bufCloserCurve)

            debugCircle.apply {
                center.set(Ox, Oy)
                radius = inscribedCircleRadius
            }
        }
    }

    private fun calcCloserCurve(
        curveStartX: Float, curveStartY: Float,
        curveBaseX: Float, curveBaseY: Float,
        curveEndX: Float, curveEndY: Float) {

        val bezier05x = findQuadBezierPoint(curveStartX, curveBaseX, curveEndX, 0.5f)
        val bezier05y = findQuadBezierPoint(curveStartY, curveBaseY, curveEndY, 0.5f)

        //Четверть изгиба = закрывающий изгиб
        val bezier025x = findQuadBezierPoint(curveStartX, curveBaseX, curveEndX, 0.25f)
        val bezier025y = findQuadBezierPoint(curveStartY, curveBaseY, curveEndY, 0.25f)

        val closerCurveStartX = bezier05x
        val closerCurveStartY = bezier05y
        val closerCurveEndX = curveStartX
        val closerCurveEndY = curveStartY
        //Базовая точка последнего (закрывающего) изгиба
        val closerCurveBaseX = findBasePointInQuadBezier(bezier025x, closerCurveStartX, closerCurveEndX, 0.5f)
        val closerCurveBaseY = findBasePointInQuadBezier(bezier025y, closerCurveStartY, closerCurveEndY, 0.5f)

        bufCloserCurve.apply {
            start.set(closerCurveStartX, closerCurveStartY)
            base.set(closerCurveBaseX, closerCurveBaseY)
            end.set(closerCurveEndX, closerCurveEndY)
        }
    }

    private fun findLineParameters(point1: PointF, point2: PointF) {
        val k = (point2.y - point1.y) / (point2.x - point1.x)
        val b = point1.y - k * point1.x
        bufLineParameters.k = k
        bufLineParameters.b = b
    }

    private fun findQuadBezierPoint(start: Float, base: Float, end: Float, @FloatRange(from = 0.0, to = 1.0) t: Float): Float {
        val firstPoint = (1 - t).pow(2) * start
        val secondPoint = 2 * t * (1 - t) * base
        val thirdPoint = t.pow(2) * end
        return firstPoint + secondPoint + thirdPoint
    }

    private fun findBasePointInQuadBezier(bezier: Float, start: Float, end: Float, @FloatRange(from = 0.0, to = 1.0) t: Float): Float {
        val firstPoint = (1 - t).pow(2) * start
        val secondWithoutPoint = 2 * t * (1 - t)
        val thirdPoint = t.pow(2) * end
        return (bezier - firstPoint - thirdPoint) / secondWithoutPoint
    }

    //Параметры k и b для уровнения прямой вида y = kx + b
    private class LineParameters {
        var k = 0f
        var b = 0f
    }
}

package ru.alex.book_pager.curl_effect.graphics

import android.graphics.Path
import android.graphics.PointF

internal fun Path.moveTo(point: PointF) {
    moveTo(point.x, point.y)
}

internal fun Path.quadTo(basePoint: PointF, endPoint: PointF) {
    quadTo(basePoint.x, basePoint.y, endPoint.x, endPoint.y)
}

internal fun Path.lineTo(point: PointF) {
    lineTo(point.x, point.y)
}

// Приводит точку к координатам, для которых написаны все расчеты
internal fun PointF.toStandard(viewWidth: Float, viewHeight: Float, isTopCorner: Boolean, isTopBend: Boolean) {
    val oldX = x
    if (isTopCorner) {
        if (isTopBend) {
            y = viewHeight - y
        } else {
            x = viewHeight - y
            y = viewHeight - (viewWidth - oldX)
        }
    } else {
        if (isTopBend) {
            x = y
            y = viewHeight - (viewWidth - oldX)
        } else {
            //Это те самые "стандартные" координаты, для которых написанны расчеты. Преобразования не нужны
        }
    }
}

// Возвращает точку к родным координатам
internal fun PointF.fromStandard(viewWidth: Float, viewHeight: Float, isTopCorner: Boolean, isTopBend: Boolean) {
    val oldX = x
    if (isTopCorner) {
        if (isTopBend) {
            y = -(y - viewHeight)
        } else {
            x = viewWidth - (viewHeight - y)
            y = viewHeight - oldX
        }
    } else {
        if (isTopBend) {
            x = viewWidth - (viewHeight - y)
            y = oldX
        } else {
            //Это те самые "стандартные" координаты, для которых написанны расчеты. Преобразования не нужны
        }
    }
}

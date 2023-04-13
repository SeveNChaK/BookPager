package ru.alex.book_pager.curl_effect.graphics

import android.graphics.Matrix
import android.graphics.PointF

class CurlBackgroundTransformation {

    companion object {
        private const val CORRECT_SCALE = 0.1f
    }

    var degreesRotate = 0f
    var rotatePoint = PointF(0f, 0f)
    var translate = PointF(0f, 0f)
    var scale = PointF(1f, 1f)
    var scalePoint = PointF(0f, 0f)

    fun set(other: CurlBackgroundTransformation) {
        scale.set(other.scale)
        translate.set(other.translate)
        degreesRotate = other.degreesRotate
        rotatePoint.set(other.rotatePoint)
    }

    fun createTransformMatrix(): Matrix {
        return Matrix().apply {
            //Порядок важен!

            //Подгоняем битмапу под размер страницы
            postScale(scale.x, scale.y)
            //Еще немного подгоняем, чтоб скрыть изогнутые углы
            postScale(scale.x + CORRECT_SCALE, scale.y + CORRECT_SCALE, scalePoint.x, scalePoint.y)
            postRotate(degreesRotate, rotatePoint.x, rotatePoint.y)
            postTranslate(translate.x, translate.y)
        }
    }
}

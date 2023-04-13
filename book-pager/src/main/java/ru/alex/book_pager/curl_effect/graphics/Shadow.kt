package ru.alex.book_pager.curl_effect.graphics

import android.graphics.PointF

internal class Shadow {
    val aboveCurlStart = PointF(0f, 0f)
    val aboveCurlEnd = PointF(0f, 0f)

    internal fun set(other: Shadow) {
        aboveCurlStart.set(other.aboveCurlStart)
        aboveCurlEnd.set(other.aboveCurlEnd)
    }
}

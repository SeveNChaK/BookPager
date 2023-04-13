package ru.alex.book_pager.curl_effect.layout

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import ru.alex.book_pager.Logger

class BookPagerSnapHelper : SnapHelper() {

    companion object {
        /**
         * !Не использовать для каких-либо вычислений!
         *
         * Нам позиция не важна, так как у нас своя логика отслеживания движущейся вью.
         *
         * Данное значение используется исключительно, чтобы не возвращать RecyclerView.NO_POSITION,
         * так как это ломает флоу работы SnapHelper.
         */
        //TODO Хоть нам и не нужно, но дописать все же поиск реальных значений, как будет время.
        private const val STUB_POSITION = 0
    }

    override fun calculateDistanceToFinalSnap(layoutManager: RecyclerView.LayoutManager, targetView: View): IntArray {
        Logger.d("CalculateDistanceToFinalSnap")

        val out = IntArray(2)
        if (targetView is RecyclerView.SmoothScroller.ScrollVectorProvider) {
            targetView.computeScrollVectorForPosition(STUB_POSITION)?.apply {
                /*
                Инверсия нужна, потому что во вью движение влево - отрицательное, вправо - положительное,
                 а в ресайклере движение наоборот: влево - положительное, вправо - отрицательное.
                 */
                out[0] = -x.toInt()
                out[1] = -y.toInt()
            }
        }

        if (out[0] != 0 || out[1] != 0) {
            if (layoutManager is BookPagerLayoutManager) {
                layoutManager.snapStatus()
            }
        }

        return out
    }

    /**
     * Метод находит targetView для calculateDistanceToFinalSnap(), после того как recyclerScrollState
     * стало равно RecyclerView.SCROLL_STATE_IDLE. Данное вью нам не важно, но так как возвращение null
     * приведет к не срабатыванию доводчика, то возращаем актуально вью, чтобы не возвращать что попало.
     */
    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        return if (layoutManager is BookPagerLayoutManager) {
            val tmpView = layoutManager.getMovingPageView()
            if (tmpView != null) {
                Logger.d("FindSnapView NOT NULL")
            } else {
                Logger.d("FindSnapView NULL")
            }
            tmpView
        } else {
            Logger.d("FindSnapView NULL")
            null
        }
    }

    /**
     * Метод нужен, чтобы получить targetView для calculateDistanceToFinalSnap() во время события fling.
     * Но данное вью нам не важно, поэтому возвращаем STUB_POSITION.
     */
    override fun findTargetSnapPosition(layoutManager: RecyclerView.LayoutManager, velocityX: Int, velocityY: Int): Int {
        return STUB_POSITION
    }
}

package ru.alex.book_pager.curl_effect.layout

import android.graphics.PointF
import android.os.Parcelable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.parcelize.Parcelize
import ru.alex.book_pager.Logger

class BookPagerLayoutManager :
    RecyclerView.LayoutManager(),
    RecyclerView.SmoothScroller.ScrollVectorProvider,
    FindVisibleItemPositionLayoutManager {

    companion object {
        //Сколько вью хотим добавлять на экран
        /*
        TODO !Не менять! Изменение данного парметра, пока что не поддерживается.
         Все расчеты сделаны только для 3 вью одновременно находящихся на экране.
         Позже надо поддержать кастомизацию.
         */
        const val VIEW_COUNT_ON_SCREEN = 3
    }

    var onBookPageChangeListener: OnBookPageChangeListener? = null
        set(value) {
            field = value
            movePageController.onBookPageChangeListener = value
        }
    private val movePageController = MovePageController()
    private val bufferMoveController = MovePageController()

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.MATCH_PARENT
        )
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount <= 0) {
            Logger.d("On layout children, but item count = 0")
            removeAndRecycleAllViews(recycler)
            return
        }

        detachAndScrapAttachedViews(recycler)

        movePageController.maxDx = 2 * width

        fill(recycler, state.itemCount)
    }

    fun snapStatus() {
        movePageController.currentMoveState.status = Status.SNAP
    }

    fun getMovingPageView(): View? {
        if (!movePageController.currentMoveState.isMove()) {
            return null
        }

        val movingViewIndex = movePageController.getMovingViewIndex(itemCount)
        return if (movingViewIndex in 0 until childCount) {
            val tmpView = getChildAt(movingViewIndex)
            if (tmpView is PageView2D) {
                return tmpView
            } else {
                null
            }
        } else {
            null
        }
    }

    override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
        /*
        Оно не вызывается, но нужно чтобы layoutManager наследовал RecyclerView.SmoothScroller.ScrollVectorProvider
        для срабатывания snapHelper во время события fling (когда сделали "бросок" на дисплее).
        Само срабатывание snapHelper во время события fling нужно, чтоб не прерывалось движение.
         */

        /*
        TODO Возможно, стоит подхачить smoothScroller и тогда будет вызываться метод, но это не точно.
         Поисследовать как будет время.
         */

        return null
    }

    private fun fill(recycler: RecyclerView.Recycler, adapterItemCount: Int) {
        if (movePageController.currentOpenPageIndex >= adapterItemCount) {
            movePageController.currentOpenPageIndex = adapterItemCount - 1
        }

        val startIndex = if (movePageController.currentOpenPageIndex - 1 >= 0) {
            movePageController.currentOpenPageIndex - 1
        } else {
            movePageController.currentOpenPageIndex
        }
        val endIndex = if (movePageController.currentOpenPageIndex + 1 < adapterItemCount) {
            movePageController.currentOpenPageIndex + 1
        } else {
            movePageController.currentOpenPageIndex
        }

        movePageController.addedViewOnScreen = endIndex - startIndex + 1

        Logger.d("Fill positions $startIndex..$endIndex; adapterItemCount = $adapterItemCount")

        val hideViewIndex = movePageController.getHideViewIndex()
        for (i in 0 until movePageController.addedViewOnScreen) {
            val view = recycler.getViewForPosition(startIndex + i)
            if (view is PageView2D) {
                //Скрываем предыдущую страницу, потому что она перевернута уже
                if (hideViewIndex != -1 && i == 0) {
                    view.move(MoveState.MOVE_STATE_HIDE)
                } else {
                    view.move(MoveState.MOVE_STATE_IDLE)
                }
            }
            measureChildWithMargins(view, 0, 0)
            layoutDecoratedWithMargins(view, 0, 0, view.measuredWidth, view.measuredHeight)
            addView(view, 0)
        }
    }

    override fun findFirstVisibleItemPosition() = movePageController.currentOpenPageIndex

    override fun findLastVisibleItemPosition() = movePageController.currentOpenPageIndex

    override fun onScrollStateChanged(state: Int) {
        when (state) {
            RecyclerView.SCROLL_STATE_IDLE -> {
                if (movePageController.currentMoveState.isSnap()) {
                    Logger.d("End moving page.")
                    if (movePageController.moved()) {
                        requestLayout()
                    }
                }
            }
        }
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        return scroll { movePageController.moveX(dx) }
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        return scroll { movePageController.moveY(dy) }
    }

    private fun scroll(moveAction: () -> Int): Int {
        bufferMoveController.set(movePageController)

        val result = moveAction.invoke()

        val moveViewIndex = movePageController.getMovingViewIndex(itemCount)
        if (moveViewIndex < 0) {
            Logger.d("Set buffer controller: $bufferMoveController to current $movePageController")
            movePageController.set(bufferMoveController)
            return 0
        }

        val viewTop = getChildAt(moveViewIndex)
        return if (viewTop is PageView2D) {
            val resultMove = viewTop.move(movePageController.currentMoveState)
            if (!resultMove) {
                Logger.d("Set buffer controller: $bufferMoveController to current $movePageController")
                movePageController.set(bufferMoveController)
            }
            result
        } else {
            0
        }
    }

    override fun canScrollHorizontally(): Boolean {
        return true
    }

    override fun canScrollVertically(): Boolean {
        return true
    }

    override fun onSaveInstanceState(): Parcelable {
        return SavedState(movePageController.currentOpenPageIndex)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            movePageController.currentOpenPageIndex = state.openPageIndex
        }
    }

    fun getCurrentPageIndex() = movePageController.currentOpenPageIndex

    @Parcelize
    private data class SavedState(val openPageIndex: Int) : Parcelable
}

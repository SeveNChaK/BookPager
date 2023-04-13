package ru.alex.book_pager.curl_effect.layout

import kotlin.math.abs

class MovePageController {

    companion object {
        //Допустимая погрешность, после чего нужно заблокировать смену направления перелистывания
        private const val INACCURACY_IS_DIRECTION_CHANGE_ENABLED = 2
    }

    var onBookPageChangeListener: OnBookPageChangeListener? = null

    private var isDirectionChangeEnabled = true
    val currentMoveState = MoveState()

    var maxDx = 0

    var currentOpenPageIndex = 0
        set(value) {
            field = value
            onBookPageChangeListener?.onCurrentPageChanged(value)
        }
    private var currentMovePageIndex = -1

    var addedViewOnScreen = 0

    fun set(other: MovePageController) {
        isDirectionChangeEnabled = other.isDirectionChangeEnabled
        currentMoveState.set(other.currentMoveState)
        maxDx = other.maxDx
        currentOpenPageIndex = other.currentOpenPageIndex
        currentMovePageIndex = other.currentMovePageIndex
        addedViewOnScreen = other.addedViewOnScreen
    }

    fun getHideViewIndex(): Int {
        return if (currentMoveState.isMove() && currentMovePageIndex == currentOpenPageIndex
            || !currentMoveState.isMove() && currentOpenPageIndex > 0) {

            addedViewOnScreen - 1
        } else {
            -1
        }
    }

    fun getMovingViewIndex(adapterItemCount: Int): Int {
        if (currentMovePageIndex < 0 || adapterItemCount == 0 || adapterItemCount == 1) {
            return -1
        }

        return when {
            addedViewOnScreen != BookPagerLayoutManager.VIEW_COUNT_ON_SCREEN -> addedViewOnScreen - 1
            currentMovePageIndex == currentOpenPageIndex -> addedViewOnScreen - 2
            else -> addedViewOnScreen - 1
        }
    }

    fun moveX(dx: Int): Int {
        val tmpMovementX = currentMoveState.movement.x - dx
        var availableDx = dx

        //Проверяем, чтобы смещение не было больше допустимого
        if (tmpMovementX.compareTo(maxDx) > 0) {
            availableDx = (dx + (tmpMovementX - maxDx)).toInt()
        } else if (tmpMovementX.compareTo(-maxDx) < 0) {
            availableDx = (dx - abs(tmpMovementX + maxDx)).toInt()
        }

        if (currentMoveState.movement.x.compareTo(0) == 0
            && currentOpenPageIndex == 0
            && availableDx < 0) { //Не даем двигатсья первой странице назад

            return 0
        } else if (currentMoveState.movement.x.compareTo(0) == 0
            && currentOpenPageIndex != 0
            && abs(addedViewOnScreen - BookPagerLayoutManager.VIEW_COUNT_ON_SCREEN) != 0
            && availableDx > 0) { //Не даем двигатсья последней странице вперед

            return 0
        }

        currentMoveState.movement.x -= availableDx

        if (!currentMoveState.isMove() && availableDx != 0) {
            currentMoveState.status = Status.MOVE
        }

        if (isDirectionChangeEnabled) {
            currentMoveState.flipDirection = when {
                currentMoveState.movement.x.compareTo(0f) < 0 -> FlipDirection.LEFT
                currentMoveState.movement.x.compareTo(0f) > 0 -> FlipDirection.RIGHT
                else -> FlipDirection.NONE
            }
        } else {
            if (currentMoveState.isFlipToLeft() != currentMoveState.movement.x.compareTo(0) < 0) {
                currentMoveState.movement.x = -1f
            }
        }

        currentMovePageIndex = when{
            currentMoveState.isFlipToLeft() -> currentOpenPageIndex
            currentMoveState.isFlipToRight() -> currentOpenPageIndex - 1
            else -> -1
        }

        /*
        Это чтобы нельзя было листать в одну сторону, а потом листать в другую.
        Можно перелистывать только в ту сторону, в которую начал.
        Чтобы сменить направление (при открывании предыдцщей страницы начать открывать следующую),
        надо убрать палец с экрана и начать двигать в другую сторону
         */
        if (currentMoveState.isMove()
            && abs(currentMoveState.movement.x).compareTo(INACCURACY_IS_DIRECTION_CHANGE_ENABLED) > 0) {

            isDirectionChangeEnabled = false
        }

        correctY()

        return availableDx
    }

    fun moveY(dy: Int): Int {
        currentMoveState.movement.y -= dy
        correctY()
        return dy
    }

    //Хак для состояния, когда страничку тянут без изгибов по углам. Без этого будет проблема с отрисовкой
    private fun correctY() {
        if (currentMoveState.movement.y.compareTo(-1) > 0 && currentMoveState.movement.y.compareTo(1) < 0) {
            currentMoveState.movement.y = 1f
        }
    }

    fun moved(): Boolean {
        val result = when {
            isMovedToNext() -> {
                currentOpenPageIndex++
                true
            }
            isMovedToPrevious() -> {
                currentOpenPageIndex--
                true
            }
            else -> {
                false
            }
        }
        resetMove()
        return result
    }

    override fun toString(): String {
        return "MovePageController{" +
            "directionChangeEnabled: $isDirectionChangeEnabled, " +
            "state: $currentMoveState, " +
            "openPageIndex: $currentOpenPageIndex, " +
            "movePageIndex: $currentMovePageIndex}"
    }

    private fun isMovedToNext() = currentMoveState.isFlipToLeft() && currentMoveState.movement.x.compareTo(-(maxDx / 2f / 2f)) <= 0

    private fun isMovedToPrevious() = currentMoveState.isFlipToRight() && currentMoveState.movement.x.compareTo(maxDx / 2f / 2f) >= 0

    private fun resetMove() {
        isDirectionChangeEnabled = true
        currentMoveState.reset()
        currentMovePageIndex = -1
    }
}

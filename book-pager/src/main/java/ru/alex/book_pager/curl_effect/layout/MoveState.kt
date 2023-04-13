package ru.alex.book_pager.curl_effect.layout

import android.graphics.PointF

class MoveState {

    companion object {
        val MOVE_STATE_IDLE = MoveState()
        val MOVE_STATE_HIDE = MoveState().apply {
            status = Status.HIDE
        }
    }

    var status = Status.IDLE

    var flipDirection = FlipDirection.NONE
    var movement = PointF(0f, 0f)

    fun isFlipDirectionNone() = flipDirection == FlipDirection.NONE

    fun isFlipToLeft() = flipDirection == FlipDirection.LEFT

    fun isFlipToRight() = flipDirection == FlipDirection.RIGHT

    fun isIdle() = status == Status.IDLE

    fun isSnap() = status == Status.SNAP

    fun isMove() = status == Status.MOVE || status == Status.SNAP

    fun isHide() = status == Status.HIDE

    fun set(other: MoveState) {
        status = other.status
        flipDirection = other.flipDirection
        movement.set(other.movement)
    }

    fun reset() {
        movement.set(0f, 0f)
        status = Status.IDLE
        flipDirection = FlipDirection.NONE
    }

    override fun toString(): String {
        return "MoveState{$status, $flipDirection, $movement}"
    }
}

enum class Status {
    IDLE, MOVE, HIDE, SNAP
}

enum class FlipDirection {
    LEFT, RIGHT, NONE
}

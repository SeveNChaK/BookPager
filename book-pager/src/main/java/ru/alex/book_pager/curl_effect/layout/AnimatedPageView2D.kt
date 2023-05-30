package ru.alex.book_pager.curl_effect.layout

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd
import ru.alex.book_pager.visible

class AnimatedPageView2D(context: Context, attrs: AttributeSet?) : PageView2D(context, attrs) {

	companion object {
		private const val SHOW_ANIMATION_DURATION = 1000L
		private const val HIDE_ANIMATION_DURATION = 1000L
	}

	private val moveState = MoveState()

	private var showAnimator = ValueAnimator().apply {
		duration = SHOW_ANIMATION_DURATION
		interpolator = AccelerateDecelerateInterpolator()
		addUpdateListener {
			val valueX = it.animatedValue as Float

			val expectedY = 2f //Это чтобы не попасть на ограничение во вью в 90 градусов

			//Хак для состояния, когда страничку тянут без изгибов по углам. Без этого будет проблема с отрисовкой
			val y = if (expectedY.compareTo(-1) > 0 && expectedY.compareTo(1) < 0) {
				2f
			} else {
				expectedY
			}

			moveState.movement.set(valueX, y)
			move(moveState)
		}
		doOnEnd {
			moveState.set(MoveState.MOVE_STATE_IDLE)
			move(moveState)
		}
	}

	private var hideAnimator = ValueAnimator().apply {
		duration = HIDE_ANIMATION_DURATION
		interpolator = AccelerateDecelerateInterpolator()
		addUpdateListener {
			val valueX = it.animatedValue as Float

			val expectedY = -2f //Это чтобы не попасть на ограничение во вью в 90 градусов

			//Хак для состояния, когда страничку тянут без изгибов по углам. Без этого будет проблема с отрисовкой
			val y = if (expectedY.compareTo(-1) > 0 && expectedY.compareTo(1) < 0) {
				-2f
			} else {
				expectedY
			}

			moveState.movement.set(valueX, y)
			move(moveState)
		}
		doOnEnd {
			moveState.set(MoveState.MOVE_STATE_HIDE)
			move(moveState)
		}
	}

	fun setupAsVisible() {
		moveState.set(MoveState.MOVE_STATE_IDLE)
		move(moveState)
	}

	fun setupAsHide() {
		moveState.set(MoveState.MOVE_STATE_HIDE)
		move(moveState)
	}

	fun showPage() {
		visible()
		if (moveState.isIdle()) {
			return
		}
		moveState.apply {
			status = Status.MOVE
			flipDirection = FlipDirection.RIGHT
			movement.set(1f, 2f)
		}
		move(moveState)

		val endX = width.toFloat() * 2
		showAnimator.apply {
			setFloatValues(1f, endX)
			start()
		}
	}

	fun hidePage() {
		if (moveState.isHide()) {
			return
		}

		moveState.apply {
			status = Status.MOVE
			flipDirection = FlipDirection.LEFT
			movement.set(-1f, -2f)
		}
		move(moveState)

		val endX = width.toFloat() * 2f
		hideAnimator.apply {
			setFloatValues(0f, -endX)
			start()
		}
	}
}

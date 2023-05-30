package ru.alex.book_pager

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import kotlin.random.Random

object Utils {

	val random = Random(System.currentTimeMillis())

	@JvmStatic
	@CheckResult
	fun withTintColorRes(
		context: Context,
		@DrawableRes drawableRes: Int,
		@ColorRes tintColorRes: Int
	): Drawable {
		return withTint(
			ContextCompat.getDrawable(context, drawableRes)!!,
			context.getColor(tintColorRes),
			PorterDuff.Mode.SRC_IN
		)
	}

	@CheckResult
	fun withTint(
		drawable: Drawable,
		@ColorInt tintColor: Int,
		tintMode: PorterDuff.Mode
	): Drawable {
		val tmpDrawable = DrawableCompat.wrap(drawable.mutate())
		DrawableCompat.setTint(tmpDrawable, tintColor)
		if (tintMode != PorterDuff.Mode.SRC_IN) {
			DrawableCompat.setTintMode(tmpDrawable, tintMode)
		}
		return tmpDrawable
	}

	@ColorInt
	fun generateColor(): Int {
		val red = random.nextInt(256).toFloat()
		val green = random.nextInt(256).toFloat()
		val blue = random.nextInt(256).toFloat()
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			Color.rgb(red, green, blue)
		} else {
			Color.DKGRAY
		}
	}
}

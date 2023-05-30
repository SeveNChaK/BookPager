package ru.alex.book_pager

import android.content.Context
import android.util.TypedValue
import android.view.View
import androidx.annotation.Px

fun View.visible() {
	if (visibility != View.VISIBLE) {
		visibility = View.VISIBLE
	}
}

fun View.hide() {
	if (visibility != View.INVISIBLE) {
		visibility = View.INVISIBLE
	}
}

fun View.gone() {
	if (visibility != View.GONE) {
		visibility = View.GONE
	}
}

@Px
fun Float.dpToPixels(): Float {
	return this.dpToPixels(ApplicationContextProvider.applicationContext)
}

@Px
fun Int.dpToPixels(): Float {
	return this.dpToPixels(ApplicationContextProvider.applicationContext)
}

@Px
fun Int.dpToPixelSize(): Int {
	return this.dpToPixelSize(ApplicationContextProvider.applicationContext)
}

@Px
fun Int.dpToPixels(context: Context): Float {
	return TypedValue.applyDimension(
		TypedValue.COMPLEX_UNIT_DIP,
		this.toFloat(),
		context.resources.displayMetrics
	)
}

@Px
fun Float.dpToPixels(context: Context): Float {
	return TypedValue.applyDimension(
		TypedValue.COMPLEX_UNIT_DIP,
		this,
		context.resources.displayMetrics
	)
}

@Px
fun Int.dpToPixelSize(context: Context): Int {
	return TypedValue.applyDimension(
		TypedValue.COMPLEX_UNIT_DIP,
		this.toFloat(),
		context.resources.displayMetrics
	).toInt()
}

@Px
fun Float.dpToPixelSize(context: Context): Int {
	return TypedValue.applyDimension(
		TypedValue.COMPLEX_UNIT_DIP,
		this,
		context.resources.displayMetrics
	).toInt()
}

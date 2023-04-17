package ru.alex.book_pager

import android.content.Context
import android.util.TypedValue
import androidx.annotation.Px

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

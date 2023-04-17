package ru.alex.book_pager.curl_effect.example

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import kotlin.random.Random
import ru.alex.book_pager.R

object StubPages {

	private val random = Random(System.currentTimeMillis())

	fun generatePages(count: Int = 5): List<Page> =
		(1..count).map {
			Page(ColorDrawable(generateColor()), generatePicture())
		}

	@ColorInt
	private fun generateColor(): Int {
		val red = random.nextInt(256).toFloat()
		val green = random.nextInt(256).toFloat()
		val blue = random.nextInt(256).toFloat()
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			Color.rgb(red, green, blue)
		} else {
			Color.DKGRAY
		}
	}

	@DrawableRes
	private fun generatePicture(count: Int = 4): List<Int> {
		val pictures = listOf(
			R.drawable.p1,
			R.drawable.p2,
			R.drawable.p3
		)
		return (1..count).map { pictures[random.nextInt(3)] }
	}
}
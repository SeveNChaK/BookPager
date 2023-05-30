package ru.alex.book_pager.curl_effect.example

import android.graphics.drawable.ColorDrawable
import androidx.annotation.DrawableRes
import ru.alex.book_pager.R
import ru.alex.book_pager.Utils
import ru.alex.book_pager.Utils.random

object StubPages {

	fun generatePages(count: Int = 5): List<Page> =
		(1..count).map {
			Page(ColorDrawable(Utils.generateColor()), generatePicture())
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
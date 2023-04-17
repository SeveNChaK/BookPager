package ru.alex.book_pager.curl_effect.example

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kotlin.random.Random
import ru.alex.book_pager.R
import ru.alex.book_pager.curl_effect.layout.BookPagerLayoutManager
import ru.alex.book_pager.curl_effect.layout.BookPagerSnapHelper

class ExampleCurlEffectFragment : Fragment() {

	private val random = Random(System.currentTimeMillis())

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(R.layout.fragment_example_curl_effect, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		requireActivity().actionBar?.title = getString(R.string.curl_effect_example_title)

		view.findViewById<RecyclerView>(R.id.pages_list).apply {
			layoutManager = BookPagerLayoutManager()
			BookPagerSnapHelper().attachToRecyclerView(this)
			adapter = PagesAdapter(generatePages())
		}
	}

	private fun generatePages(count: Int = 5): List<Page> =
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

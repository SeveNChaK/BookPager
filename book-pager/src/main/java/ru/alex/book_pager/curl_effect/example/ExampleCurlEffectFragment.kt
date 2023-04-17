package ru.alex.book_pager.curl_effect.example

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import ru.alex.book_pager.R
import ru.alex.book_pager.curl_effect.layout.BookPagerLayoutManager
import ru.alex.book_pager.curl_effect.layout.BookPagerSnapHelper

class ExampleCurlEffectFragment : Fragment() {

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
			adapter = PagesAdapter(StubPages.generatePages())
		}
	}
}

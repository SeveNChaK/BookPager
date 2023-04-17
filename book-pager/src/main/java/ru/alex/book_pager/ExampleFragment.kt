package ru.alex.book_pager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import ru.alex.book_pager.curl_effect.example.ExampleCurlEffectFragment

class ExampleFragment : Fragment() {

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(R.layout.fragment_example_main, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val examplesContainer = view.findViewById<LinearLayout>(R.id.examples_container)

		for (item in ExampleItem.values()) {
			val itemView = layoutInflater.inflate(
				R.layout.item_example,
				examplesContainer,
				false
			) as TextView
			itemView.apply {
				text = item.title
				setOnClickListener { onItemClick(item) }
			}
			examplesContainer.addView(itemView)
		}
	}

	private fun onItemClick(item: ExampleItem) {
		val navigatorActivity = requireActivity() as ExampleActivity
		navigatorActivity.navigateTo(item.classFragment)
	}

	private enum class ExampleItem(
		val title: String,
		val classFragment: Class<out Fragment>
	) {
		CURL_EFFECT("Листалка", ExampleCurlEffectFragment::class.java),
		;
	}
}

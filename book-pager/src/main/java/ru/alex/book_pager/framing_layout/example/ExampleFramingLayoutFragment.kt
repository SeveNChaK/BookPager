package ru.alex.book_pager.framing_layout.example

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.alex.book_pager.R
import ru.alex.book_pager.framing_layout.CustomFramingLayout

class ExampleFramingLayoutFragment : Fragment()  {

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(R.layout.fragment_example_framing_layout, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val framingLayout = view.findViewById<CustomFramingLayout>(R.id.framing_layout_content_container)

		view.findViewById<View>(R.id.type_stroke).setOnClickListener {
			framingLayout.apply {
				setupRenderer(StubFrames.createStrokeRenderer())
				requestLayout()
			}
		}
		view.findViewById<View>(R.id.type_rect_corners).setOnClickListener {
			framingLayout.apply {
				setupRenderer(StubFrames.createPathCornersRenderer(isPaintedCorner = false))
				requestLayout()
			}
		}
		view.findViewById<View>(R.id.type_painted_rect_corners).setOnClickListener {
			framingLayout.apply {
				setupRenderer(StubFrames.createPathCornersRenderer(isPaintedCorner = true))
				requestLayout()
			}
		}
	}
}

package ru.alex.book_pager.design_editor

import ru.alex.book_pager.R
import ru.alex.book_pager.framing_layout.renderer.FrameType

object StubTypes {

	val coverTypes = listOf(
		CoverTypeItem(
			R.drawable.gz,
			R.drawable.cover_color_1
		),
		CoverTypeItem(
			R.drawable.gz,
			R.drawable.cover_color_2
		),
		CoverTypeItem(
			R.drawable.gz,
			R.drawable.cover_color_3
		)
	)
	val backgroundTypes = listOf(
		BackgroundTypeItem(R.drawable.cover_color_1),
		BackgroundTypeItem(R.drawable.cover_color_2),
		BackgroundTypeItem(R.drawable.cover_color_3)
	)
	val frameTypes = listOf(
		FrameTypeItem(
			id = 0,
			FrameType.Stroke("#FFFFFF", widthDP = 6, shadow = false)
		)
	)

	val coverPreviewFrameType = FrameType.Stroke("#FFFFFF", widthDP = 6, radiusDP = 6, shadow = true)
}

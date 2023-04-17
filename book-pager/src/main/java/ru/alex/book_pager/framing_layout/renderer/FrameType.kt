package ru.alex.book_pager.framing_layout.renderer

import android.graphics.Path

sealed class FrameType {
	data class Stroke(val color: String, val widthDP: Int, val radiusDP: Int = 0) : FrameType()
	data class PathCorners(
		val cornerSizeDP: Int,
		val offsetDP: Int,
		val color: String,
		val path: Path
	) : FrameType()
}

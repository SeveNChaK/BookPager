package ru.alex.book_pager.framing_layout.example

import android.graphics.Path
import ru.alex.book_pager.framing_layout.renderer.FrameType
import ru.alex.book_pager.framing_layout.renderer.LinesRenderer
import ru.alex.book_pager.framing_layout.renderer.PathCornersRenderer
import ru.alex.book_pager.dpToPixels

object StubFrames {
	private const val DEFAULT_FRAME_COLOR = "#000000"

	private const val STROKE_WIDTH_DP = 16
	private const val STROKE_RADIUS_DP = 8
	private const val STROKE_COLOR = DEFAULT_FRAME_COLOR

	private const val CORNER_SIZE_DP = 64
	private const val CORNER_OFFSET_DP = 16
	private const val CORNER_COLOR = DEFAULT_FRAME_COLOR

	private val RECT_CORNER_PATH by lazy {
		Path().apply {
			val cornerSize = CORNER_SIZE_DP.dpToPixels()
			moveTo(0f, 0f)
			lineTo(cornerSize, 0f)
			lineTo(cornerSize, cornerSize * 0.2f)
			lineTo(cornerSize * 0.2f, cornerSize * 0.2f)
			lineTo(cornerSize * 0.2f, cornerSize)
			lineTo(0f, cornerSize)
			lineTo(0f, 0f)
		}
	}
	private val PAINTED_RECT_CORNER_PATH by lazy {
		val cornerSize = CORNER_SIZE_DP.dpToPixels()
		Path().apply {
			moveTo(0f, 0f)
			lineTo(cornerSize, 0f)
			lineTo(cornerSize, cornerSize * 0.2f)
			lineTo(cornerSize * 0.2f, cornerSize)
			lineTo(0f, cornerSize)
			lineTo(0f, 0f)
		}
	}

	fun createStrokeRenderer(): LinesRenderer {
		val frameType = FrameType.Stroke(STROKE_COLOR, STROKE_WIDTH_DP, STROKE_RADIUS_DP)
		return LinesRenderer(frameType)
	}

	fun createPathCornersRenderer(isPaintedCorner: Boolean): PathCornersRenderer {
		val path = if (isPaintedCorner) {
			PAINTED_RECT_CORNER_PATH
		} else {
			RECT_CORNER_PATH
		}
		val frameType = FrameType.PathCorners(CORNER_SIZE_DP, CORNER_OFFSET_DP, CORNER_COLOR, path)
		return PathCornersRenderer(frameType)
	}
}

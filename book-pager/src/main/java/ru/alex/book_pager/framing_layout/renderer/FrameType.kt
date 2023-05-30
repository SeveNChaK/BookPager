package ru.alex.book_pager.framing_layout.renderer

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

sealed class FrameType : Parcelable {
	abstract fun getRenderer(): BaseFrameRenderer

	@Parcelize
	data class Stroke(val color: String, val widthDP: Int, val radiusDP: Int = 0, val shadow: Boolean = false) : FrameType() {
		override fun getRenderer() = LinesRenderer(this)
	}
	@Parcelize
	data class Corner(@DrawableRes val cornerRes: Int, val cornerSizeDP: Int, val offsetDP: Int, val shadow: Boolean) : FrameType() {
		override fun getRenderer() = CornersRenderer(this)
	}
}

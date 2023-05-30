package ru.alex.book_pager.design_editor

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView

class HorizontalSpacingDecoration(
	@Px private val s: Int,
	@Px private val p: Int
) : ItemDecoration() {

	private var maxItemsCountInRow = 0

	override fun getItemOffsets(
		outRect: Rect,
		view: View,
		parent: RecyclerView,
		state: RecyclerView.State
	) { getItemOffsetsForLinear(outRect, view, parent) }

	private fun getItemOffsetsForLinear(outRect: Rect, view: View, parent: RecyclerView) {
		val lp = view.layoutParams as RecyclerView.LayoutParams
		if (lp.viewAdapterPosition == RecyclerView.NO_POSITION) {
			// this view may have been removed
			return
		}
		val position = lp.viewLayoutPosition
		if (p == 0) {
			outRect.right = if (position == parent.adapter!!.itemCount - 1) 0 else s
			return
		}
		maxItemsCountInRow = parent.adapter!!.itemCount
		getOffsetsForItemInRow(outRect, position, true, maxItemsCountInRow - 1 - position)
	}

	private fun getOffsetsForItemInRow(
		outRect: Rect,
		itemsCountToTheLeft: Int,
		fullRow: Boolean,
		realItemsCountToTheRight: Int
	) {
		val n = if (fullRow) itemsCountToTheLeft + realItemsCountToTheRight else maxItemsCountInRow - 1
		val s_1 = (n * s - (n - 1) * p) / (n + 1)
		val itemsCountToTheRight = n - itemsCountToTheLeft
		if (itemsCountToTheLeft == 0) {
			outRect.left = p
			outRect.right = s_1
		} else if (itemsCountToTheRight == 0) {
			outRect.left = s_1
			outRect.right = p
		} else if (itemsCountToTheLeft == itemsCountToTheRight) {
			outRect.right = (s * n + 2 * p) / (2 * (n + 1))
			outRect.left = outRect.right
		} else if (itemsCountToTheLeft < itemsCountToTheRight) {
			outRect.left = itemsCountToTheLeft * (s - s_1) - (itemsCountToTheLeft - 1) * p
			outRect.right = (itemsCountToTheLeft + 1) * s_1 + itemsCountToTheLeft * (p - s)
		} else {
			outRect.left = (itemsCountToTheRight + 1) * s_1 + itemsCountToTheRight * (p - s)
			outRect.right = itemsCountToTheRight * (s - s_1) - (itemsCountToTheRight - 1) * p
		}
	}
}

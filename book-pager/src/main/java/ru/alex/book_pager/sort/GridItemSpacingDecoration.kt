package ru.alex.book_pager.sort

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager

class GridItemSpacingDecoration(private val itemOffset: Int) : ItemDecoration() {

	override fun getItemOffsets(
		outRect: Rect,
		view: View,
		parent: RecyclerView,
		state: RecyclerView.State
	) {
		val position = parent.getChildAdapterPosition(view)
		val adapter = parent.adapter
		if (adapter == null || position == RecyclerView.NO_POSITION) {
			return
		}
		val layoutManager = parent.layoutManager
		if (layoutManager is GridLayoutManager) {
			val spanCount = layoutManager.spanCount
			val layoutParams = view.layoutParams as GridLayoutManager.LayoutParams
			val firstSpanColumn = layoutParams.spanIndex
			val lastSpanColumn = firstSpanColumn + layoutParams.spanSize - 1
			outRect.left = firstSpanColumn * itemOffset / spanCount
			outRect.right = itemOffset - (lastSpanColumn + 1) * itemOffset / spanCount
			outRect.top = itemOffset // item top
			val quantityItemsInLastLine = adapter.itemCount % spanCount
			if (position >= adapter.itemCount - quantityItemsInLastLine && position < adapter.itemCount) {
				outRect.bottom = itemOffset //last items bottom
			}
		}
	}
}

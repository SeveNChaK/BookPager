package ru.alex.book_pager.sort

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ReorderTouchCallback(
	private val onMoved: (Int, Int) -> Unit,
	private val onDropped: () -> Unit
) : ItemTouchHelper.Callback() {

	companion object {
		private const val INVALID_POSITION = -1
		private const val dragFlags = (ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
	}

	private var dragEnabled = true

	private var fromPos = INVALID_POSITION
	private var toPos = INVALID_POSITION

	override fun getMovementFlags(
		recyclerView: RecyclerView,
		viewHolder: RecyclerView.ViewHolder
	) = makeMovementFlags(if (dragEnabled) dragFlags else 0, 0)

	override fun onMove(
		recyclerView: RecyclerView,
		source: RecyclerView.ViewHolder,
		target: RecyclerView.ViewHolder
	): Boolean {
		if (source.itemViewType != target.itemViewType) {
			return false
		}
		val fromAdapterPos = source.adapterPosition
		val toAdapterPos = target.adapterPosition
		if (fromPos == INVALID_POSITION) {
			fromPos = fromAdapterPos
		}
		toPos = toAdapterPos
		onMoved.invoke(fromAdapterPos, toAdapterPos)
		return true
	}

	override fun clearView(
		recyclerView: RecyclerView,
		viewHolder: RecyclerView.ViewHolder
	) {
		super.clearView(recyclerView, viewHolder)
		if (fromPos != toPos && fromPos != INVALID_POSITION) {
			onDropped.invoke()
		}
		fromPos = INVALID_POSITION
		toPos = INVALID_POSITION
	}

	override fun isItemViewSwipeEnabled() = false

	override fun isLongPressDragEnabled() = dragEnabled

	override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
		/* No swipe action */
	}
}

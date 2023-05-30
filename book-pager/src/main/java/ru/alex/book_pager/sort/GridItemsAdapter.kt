package ru.alex.book_pager.sort

import android.graphics.Color
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.parcelize.Parcelize
import ru.alex.book_pager.R

@Parcelize
data class Item(val id: Int) : Parcelable

class GridItemsAdapter(
	private val onItemDragged: (holder: ItemViewHolder) -> Unit,
) : PagedListAdapter<Item, ItemViewHolder>(DIFF_CALLBACK) {

	companion object {
		val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Item>() {
			override fun areItemsTheSame(oldItem: Item, newItem: Item) = true

			override fun areContentsTheSame(oldItem: Item, newItem: Item) = oldItem == newItem
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(
			R.layout.item_sort_grid,
			parent,
			false
		)
		return ItemViewHolder(view,
			onStartDragAction = { holder -> onItemDragged(holder) }
		)
	}

	override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
		val item = getItem(position) ?: return
		holder.bind(item)
	}

	override fun getItemCount() = currentList?.size ?: 0
}

class ItemViewHolder(
	itemView: View,
	onStartDragAction: (ItemViewHolder) -> Unit
) : RecyclerView.ViewHolder(itemView) {

	private val itemImage = itemView.findViewById<ItemView>(R.id.item_image)
	private val itemNumber = itemView.findViewById<TextView>(R.id.item_number)

	init {
		itemImage.apply {
			setState(ItemView.State.DRAGGABLE)
			setBackgroundColor(Color.LTGRAY)
			setOnItemDragListener { onStartDragAction(this@ItemViewHolder) }
		}
	}

	fun bind(item: Item) {
		itemNumber.text = item.id.toString()
	}
}

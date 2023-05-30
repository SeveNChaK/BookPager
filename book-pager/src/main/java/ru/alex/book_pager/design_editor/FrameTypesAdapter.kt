package ru.alex.book_pager.design_editor

import android.annotation.SuppressLint
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.parcelize.Parcelize
import ru.alex.book_pager.R
import ru.alex.book_pager.dpToPixels
import ru.alex.book_pager.framing_layout.CustomFramingLayout
import ru.alex.book_pager.framing_layout.renderer.FrameType


@Parcelize
data class FrameTypeItem(
	val id: Int,
	val type: FrameType
) : Parcelable

class FrameTypesAdapter(
	private val onFrameTypeClickListener: OnFrameTypeClickListener
) : RecyclerView.Adapter<FrameTypeViewHolder>() {

	private val frameTypes = mutableListOf<FrameTypeItem>()
	private var selectedPosition = 0

	@SuppressLint("NotifyDataSetChanged")
	fun setItems(types: List<FrameTypeItem>, selectedType: FrameTypeItem) {
		frameTypes.apply {
			clear()
			addAll(types)
		}
		//TODO тип надо добавить им
		val selectedIndex = types.indexOfFirst { it.id == selectedType.id }
		selectedPosition = if (selectedIndex >= 0) {
			selectedIndex
		} else {
			0
		}
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FrameTypeViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(
			R.layout.item_editor_frame_type,
			parent,
			false
		)
		return FrameTypeViewHolder(view) { position ->
			if (selectedPosition == position) return@FrameTypeViewHolder

			notifyItemChanged(selectedPosition, CLEARED)
			selectedPosition = position
			notifyItemChanged(position, SELECTED)

			val frameType = frameTypes[position]
			onFrameTypeClickListener.onFrameSelect(frameType)
		}
	}

	override fun onBindViewHolder(holder: FrameTypeViewHolder, position: Int) =
		holder.bind(frameTypes[position], selectedPosition == position)

	override fun onBindViewHolder(holder: FrameTypeViewHolder, position: Int, payloads: MutableList<Any>) {
		if (payloads.size == 1) {
			holder.bind(frameTypes[position], payloads[0] == SELECTED)
		} else {
			onBindViewHolder(holder, position)
		}
	}

	override fun getItemCount() = frameTypes.size

	private companion object {
		const val SELECTED = "selected"
		const val CLEARED = "cleared"
	}
}

class FrameTypeViewHolder(itemView: View, onClickAction: (Int) -> Unit) : RecyclerView.ViewHolder(itemView) {

	private val customLayersFrameLayout: CustomFramingLayout = itemView.findViewById(R.id.frame_layout)
	private val checkbox: ImageView = itemView.findViewById(R.id.preview_checkbox)

	init {
		itemView.setOnClickListener { onClickAction(adapterPosition) }
	}

	fun bind(frameType: FrameTypeItem, isSelect: Boolean) {
		customLayersFrameLayout.setupRenderer(frameType.type.getRenderer())
		customLayersFrameLayout.elevation = 2.dpToPixels()
		bindSelection(isSelect)
	}

	private fun bindSelection(isSelect: Boolean) {
		val checkboxRes = if (isSelect) {
			R.drawable.ic_image_radio_on
		} else {
			R.drawable.image_radio_off
		}
		checkbox.setImageResource(checkboxRes)
	}
}

interface OnFrameTypeClickListener {
	fun onFrameSelect(frameType: FrameTypeItem)
}

package ru.alex.book_pager.design_editor

import android.annotation.SuppressLint
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.parcelize.Parcelize
import ru.alex.book_pager.R
import ru.alex.book_pager.framing_layout.CustomFramingLayout

@Parcelize
data class CoverTypeItem(
	@DrawableRes val previewImageRes: Int,
	@DrawableRes val coverImageRes: Int
) : Parcelable

class CoverTypesAdapter(
	private val onCoverSelectListener: OnCoverSelectListener
) : RecyclerView.Adapter<CoverTypeViewHolder>() {

	private val coverTypes = mutableListOf<CoverTypeItem>()
	private var selectedPosition = 0

	@SuppressLint("NotifyDataSetChanged")
	fun setItems(types: List<CoverTypeItem>, selectedType: CoverTypeItem) {
		coverTypes.apply {
			clear()
			addAll(types)
		}
		val selectedIndex = types.indexOfFirst { it.coverImageRes == selectedType.coverImageRes }
		selectedPosition = if (selectedIndex >= 0) {
			selectedIndex
		} else {
			0
		}
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoverTypeViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(
			R.layout.item_editor_preview,
			parent,
			false
		)
		return CoverTypeViewHolder(view) { onSelect(it) }
	}

	override fun onBindViewHolder(holder: CoverTypeViewHolder, position: Int) {
		holder.bind(coverTypes[position], selectedPosition == position)
	}

	override fun onBindViewHolder(holder: CoverTypeViewHolder, position: Int, payloads: MutableList<Any>) {
		if (payloads.size == 1) {
			holder.bind(coverTypes[position], payloads[0] == SELECTED)
		} else {
			onBindViewHolder(holder, position)
		}
	}

	override fun getItemCount() = coverTypes.size

	private fun onSelect(position: Int) {
		if (selectedPosition == position) return

		notifyItemChanged(selectedPosition, CLEARED)
		selectedPosition = position
		notifyItemChanged(position, SELECTED)

		val coverType = coverTypes[position]
		onCoverSelectListener.onCoverSelect(coverType)
	}

	private companion object {
		const val SELECTED = "selected"
		const val CLEARED = "cleared"
	}
}

class CoverTypeViewHolder(
	itemView: View,
	onSelectAction: (Int) -> Unit
) : ViewHolder(itemView) {

	private val backgroundView: View = itemView.findViewById(R.id.preview_background)
	private val customFramingLayout: CustomFramingLayout = itemView.findViewById(R.id.frame_layout)
	private val previewImage: ImageView = itemView.findViewById(R.id.preview_image)
	private val checkbox: ImageView = itemView.findViewById(R.id.preview_checkbox)

	init {
		customFramingLayout.setupRenderer(StubTypes.coverPreviewFrameType.getRenderer())
		itemView.setOnClickListener { onSelectAction(adapterPosition) }
	}

	fun bind(coverType: CoverTypeItem, isSelect: Boolean) {
		backgroundView.setBackgroundResource(coverType.coverImageRes)
		previewImage.setImageResource(coverType.previewImageRes)
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

interface OnCoverSelectListener {
	fun onCoverSelect(coverType: CoverTypeItem)
}

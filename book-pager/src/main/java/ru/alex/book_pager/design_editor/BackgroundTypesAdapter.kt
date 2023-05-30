package ru.alex.book_pager.design_editor

import android.annotation.SuppressLint
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import kotlinx.parcelize.Parcelize
import ru.alex.book_pager.R
import ru.alex.book_pager.framing_layout.CustomFramingLayout
import ru.alex.book_pager.gone

@Parcelize
data class BackgroundTypeItem(@DrawableRes val backgroundRes: Int) : Parcelable

class BackgroundTypesAdapter(
	private val onBackgroundTypeSelectListener: OnBackgroundTypeSelectListener
) : RecyclerView.Adapter<BackgroundTypeViewHolder>() {

	private val backgroundTypes = mutableListOf<BackgroundTypeItem>()
	private var selectedPosition = 0

	@SuppressLint("NotifyDataSetChanged")
	fun setItems(types: List<BackgroundTypeItem>, selectedType: BackgroundTypeItem) {
		backgroundTypes.apply {
			clear()
			addAll(types)
		}
		val selectedIndex = types.indexOfFirst { it.backgroundRes == selectedType.backgroundRes }
		selectedPosition = if (selectedIndex >= 0) {
			selectedIndex
		} else {
			0
		}
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackgroundTypeViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(
			R.layout.item_editor_preview,
			parent,
			false
		)
		return BackgroundTypeViewHolder(view) { position ->
			if (selectedPosition == position) return@BackgroundTypeViewHolder

			notifyItemChanged(selectedPosition, CLEARED)
			selectedPosition = position
			notifyItemChanged(position, SELECTED)

			val backgroundType = backgroundTypes[position]
			onBackgroundTypeSelectListener.onBackgroundTypeSelect(backgroundType)
		}
	}

	override fun onBindViewHolder(holder: BackgroundTypeViewHolder, position: Int) {
		holder.bind(backgroundTypes[position], selectedPosition == position)
	}

	override fun onBindViewHolder(holder: BackgroundTypeViewHolder, position: Int, payloads: MutableList<Any>) {
		if (payloads.size == 1) {
			holder.bind(backgroundTypes[position], payloads[0] == SELECTED)
		} else {
			onBindViewHolder(holder, position)
		}
	}

	override fun getItemCount() = backgroundTypes.size

	private companion object {
		const val SELECTED = "selected"
		const val CLEARED = "cleared"
	}
}

class BackgroundTypeViewHolder(itemView: View, onClickAction: (Int) -> Unit) : RecyclerView.ViewHolder(itemView) {

	private val backgroundView: View = itemView.findViewById(R.id.preview_background)
	private val customLayersFrameLayout: CustomFramingLayout = itemView.findViewById(R.id.frame_layout)
	private val previewImage: ImageView = itemView.findViewById(R.id.preview_image)
	private val checkbox: ImageView = itemView.findViewById(R.id.preview_checkbox)

	init {
		customLayersFrameLayout.gone()
		previewImage.gone()
		itemView.setOnClickListener { onClickAction(adapterPosition) }
	}

	fun bind(backgroundType: BackgroundTypeItem, isSelect: Boolean) {
		backgroundView.setBackgroundResource(backgroundType.backgroundRes)
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

interface OnBackgroundTypeSelectListener {
	fun onBackgroundTypeSelect(backgroundType: BackgroundTypeItem)
}

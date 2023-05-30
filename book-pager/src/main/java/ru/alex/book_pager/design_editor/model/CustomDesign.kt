package ru.alex.book_pager.design_editor.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.alex.book_pager.design_editor.BackgroundTypeItem
import ru.alex.book_pager.design_editor.CoverTypeItem
import ru.alex.book_pager.design_editor.FrameTypeItem

@Parcelize
data class CustomDesign(
	val coverType: CoverTypeItem,
	val backgroundType: BackgroundTypeItem,
	val frameType: FrameTypeItem
) : Parcelable

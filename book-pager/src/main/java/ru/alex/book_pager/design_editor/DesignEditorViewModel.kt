package ru.alex.book_pager.design_editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.alex.book_pager.SingleLiveData
import ru.alex.book_pager.design_editor.model.CustomDesign

class DesignEditorViewModel(
	private val oldDesign: CustomDesign?
) : ViewModel() {

	private val _viewState = MutableLiveData<PhotoBookDesignEditorViewState>()
	val viewStateLD: LiveData<PhotoBookDesignEditorViewState>
		get() = _viewState
	private val _events = SingleLiveData<PhotoBookDesignEditorEvent>()
	val eventsLD: LiveData<PhotoBookDesignEditorEvent>
		get() = _events

	var coverTypes: List<CoverTypeItem>? = null
	var backgroundTypes: List<BackgroundTypeItem>? = null
	var frameTypes: List<FrameTypeItem>? = null

	private var selectedCover: CoverTypeItem? = null
	private var selectedBackground: BackgroundTypeItem? = null
	private var selectedFrame: FrameTypeItem? = null

	fun loadDesigns() {
		_viewState.value = PhotoBookDesignEditorViewState.Loading

		coverTypes = StubTypes.coverTypes
		backgroundTypes = StubTypes.backgroundTypes
		frameTypes = StubTypes.frameTypes

		trySetLoadedState()
	}

	fun clickSave() {
		val customDesignSettings = CustomDesign(
			selectedCover!!,
			selectedBackground!!,
			selectedFrame!!
		)
		val result = ExampleDesignEditorFragment.Result(
			isSubmit = true,
			customDesignSettings
		)
		_events.setValue(PhotoBookDesignEditorEvent.SaveDesign(result))
	}

	fun onCoverSelect(coverTypeItem: CoverTypeItem) {
		selectedCover = coverTypeItem
		toCoverSelector()
	}

	fun onBackgroundSelect(backgroundTypeItem: BackgroundTypeItem) {
		selectedBackground = backgroundTypeItem
		toBackgroundSelector()
	}

	fun onFrameSelect(frameTypeItem: FrameTypeItem) {
		selectedFrame = frameTypeItem
		toFrameSelector()
	}

	fun toMainSelector() {
		_viewState.value = PhotoBookDesignEditorViewState.MainSelector(
			selectedCover!!,
			selectedBackground!!,
			selectedFrame!!,
			showCover = _viewState.value is PhotoBookDesignEditorViewState.CoverSelector
		)
	}

	fun toCoverSelector() {
		_viewState.value = PhotoBookDesignEditorViewState.CoverSelector(selectedCover!!)
	}

	fun toBackgroundSelector() {
		_viewState.value = PhotoBookDesignEditorViewState.BackgroundSelector(
			selectedBackground!!,
			selectedFrame!!,
		)
	}

	fun toFrameSelector() {
		_viewState.value = PhotoBookDesignEditorViewState.FrameSelector(
			selectedBackground!!,
			selectedFrame!!,
		)
	}

	private fun trySetLoadedState() {
		if (!coverTypes.isNullOrEmpty() && !backgroundTypes.isNullOrEmpty() && !frameTypes.isNullOrEmpty()) {
			selectedCover = oldDesign?.coverType ?: coverTypes!![0]
			selectedBackground = oldDesign?.backgroundType ?: backgroundTypes!![0]
			selectedFrame = oldDesign?.frameType ?: frameTypes!![0]
			_events.setValue(
				PhotoBookDesignEditorEvent.LoadedDesigns(
					selectedCover!!, coverTypes!!,
					selectedBackground!!, backgroundTypes!!,
					selectedFrame!!, frameTypes!!
				)
			)
			_viewState.value = PhotoBookDesignEditorViewState.MainSelector(
				selectedCover!!,
				selectedBackground!!,
				selectedFrame!!,
				showCover = false
			)
		}
	}

	class Factory(private val oldDesign: CustomDesign?) : ViewModelProvider.Factory {
		override fun <T : ViewModel> create(modelClass: Class<T>): T {
			return DesignEditorViewModel(oldDesign) as T
		}
	}
}

sealed class PhotoBookDesignEditorViewState {
	object Loading : PhotoBookDesignEditorViewState()
	class Error(val errorText: String) : PhotoBookDesignEditorViewState()

	class MainSelector(
		val selectedCover: CoverTypeItem,
		val selectedBackground: BackgroundTypeItem,
		val selectedFrame: FrameTypeItem,
		val showCover: Boolean
	) : PhotoBookDesignEditorViewState()
	class CoverSelector(val selectedCover: CoverTypeItem) : PhotoBookDesignEditorViewState()
	class BackgroundSelector(
		val selectedBackground: BackgroundTypeItem,
		val selectedFrame: FrameTypeItem,
	) : PhotoBookDesignEditorViewState()
	class FrameSelector(
		val selectedBackground: BackgroundTypeItem,
		val selectedFrame: FrameTypeItem,
	) : PhotoBookDesignEditorViewState()
}

sealed class PhotoBookDesignEditorEvent {
	class LoadedDesigns(
		val selectedCover: CoverTypeItem,
		val coverTypes: List<CoverTypeItem>,
		val selectedBackground: BackgroundTypeItem,
		val backgroundTypes: List<BackgroundTypeItem>,
		val selectedFrame: FrameTypeItem,
		val frameTypes: List<FrameTypeItem>
	) : PhotoBookDesignEditorEvent()
	class SaveDesign(
		val result: ExampleDesignEditorFragment.Result
	) : PhotoBookDesignEditorEvent()
}

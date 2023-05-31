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

	private val _viewState = MutableLiveData<DesignEditorViewState>()
	val viewStateLD: LiveData<DesignEditorViewState>
		get() = _viewState
	private val _events = SingleLiveData<DesignEditorEvent>()
	val eventsLD: LiveData<DesignEditorEvent>
		get() = _events

	var coverTypes: List<CoverTypeItem>? = null
	var backgroundTypes: List<BackgroundTypeItem>? = null
	var frameTypes: List<FrameTypeItem>? = null

	private var selectedCover: CoverTypeItem? = null
	private var selectedBackground: BackgroundTypeItem? = null
	private var selectedFrame: FrameTypeItem? = null

	fun loadDesigns() {
		_viewState.value = DesignEditorViewState.Loading

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
		_events.setValue(DesignEditorEvent.SaveDesign(result))
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
		_viewState.value = DesignEditorViewState.MainSelector(
			selectedCover!!,
			selectedBackground!!,
			selectedFrame!!,
			showCover = _viewState.value is DesignEditorViewState.CoverSelector
		)
	}

	fun toCoverSelector() {
		_viewState.value = DesignEditorViewState.CoverSelector(selectedCover!!)
	}

	fun toBackgroundSelector() {
		_viewState.value = DesignEditorViewState.BackgroundSelector(
			selectedBackground!!,
			selectedFrame!!,
		)
	}

	fun toFrameSelector() {
		_viewState.value = DesignEditorViewState.FrameSelector(
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
				DesignEditorEvent.LoadedDesigns(
					selectedCover!!, coverTypes!!,
					selectedBackground!!, backgroundTypes!!,
					selectedFrame!!, frameTypes!!
				)
			)
			_viewState.value = DesignEditorViewState.MainSelector(
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

sealed class DesignEditorViewState {
	object Loading : DesignEditorViewState()
	class Error(val errorText: String) : DesignEditorViewState()

	class MainSelector(
		val selectedCover: CoverTypeItem,
		val selectedBackground: BackgroundTypeItem,
		val selectedFrame: FrameTypeItem,
		val showCover: Boolean
	) : DesignEditorViewState()
	class CoverSelector(val selectedCover: CoverTypeItem) : DesignEditorViewState()
	class BackgroundSelector(
		val selectedBackground: BackgroundTypeItem,
		val selectedFrame: FrameTypeItem,
	) : DesignEditorViewState()
	class FrameSelector(
		val selectedBackground: BackgroundTypeItem,
		val selectedFrame: FrameTypeItem,
	) : DesignEditorViewState()
}

sealed class DesignEditorEvent {
	class LoadedDesigns(
		val selectedCover: CoverTypeItem,
		val coverTypes: List<CoverTypeItem>,
		val selectedBackground: BackgroundTypeItem,
		val backgroundTypes: List<BackgroundTypeItem>,
		val selectedFrame: FrameTypeItem,
		val frameTypes: List<FrameTypeItem>
	) : DesignEditorEvent()
	class SaveDesign(
		val result: ExampleDesignEditorFragment.Result
	) : DesignEditorEvent()
}

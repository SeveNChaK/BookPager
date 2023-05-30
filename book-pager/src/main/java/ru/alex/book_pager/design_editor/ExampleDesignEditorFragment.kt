package ru.alex.book_pager.design_editor

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import kotlinx.parcelize.Parcelize
import ru.alex.book_pager.R
import ru.alex.book_pager.Utils
import ru.alex.book_pager.curl_effect.layout.AnimatedPageView2D
import ru.alex.book_pager.design_editor.model.CustomDesign
import ru.alex.book_pager.dpToPixelSize
import ru.alex.book_pager.framing_layout.CustomFramingLayout
import ru.alex.book_pager.gone
import ru.alex.book_pager.hide
import ru.alex.book_pager.visible

class ExampleDesignEditorFragment : Fragment() {

	private lateinit var toolbar: Toolbar
	private lateinit var doneBtn: View
//	private lateinit var emptyView: SmartEmptyViewAnimated
	private lateinit var albumTitleView: TextView
	private lateinit var coverPageView: AnimatedPageView2D
	private lateinit var coverFramingLayout: CustomFramingLayout
	private lateinit var coverPreviewImage: ImageView
	private lateinit var contentPageView: FrameLayout
	private lateinit var contentFramingLayout: CustomFramingLayout
	private lateinit var contentImageView: ImageView
	private lateinit var mainSelectorGroup: Group
	private lateinit var selectCoverBtn: TextView
	private lateinit var selectBackgroundBtn: TextView
	private lateinit var selectFrameBtn: TextView
	private lateinit var bottomPanelGroup: Group
	private lateinit var bottomPanelToolbox: FrameLayout
	private lateinit var bottomPanelContainer: FrameLayout

	private lateinit var coverTypesToolboxView: ViewGroup
	private lateinit var coverTypesBottomPanelView: RecyclerView
	private val coverTypesAdapter by lazy {
		CoverTypesAdapter(
			object : OnCoverSelectListener {
				override fun onCoverSelect(coverType: CoverTypeItem) {
					viewModel.onCoverSelect(coverType)
				}
			}
		)
	}
	private lateinit var backgroundTypesToolboxView: ViewGroup
	private lateinit var backgroundTypesBottomPanelView: RecyclerView
	private val backgroundTypesAdapter by lazy {
		BackgroundTypesAdapter(
			object : OnBackgroundTypeSelectListener {
				override fun onBackgroundTypeSelect(backgroundType: BackgroundTypeItem) {
					viewModel.onBackgroundSelect(backgroundType)
				}
			}
		)
	}
	private lateinit var frameToolboxView: ViewGroup
	private lateinit var frameBottomPanelView: RecyclerView
	private val frameTypesAdapter by lazy {
		FrameTypesAdapter(
			object : OnFrameTypeClickListener {
				override fun onFrameSelect(frameType: FrameTypeItem) {
					viewModel.onFrameSelect(frameType)
				}
			}
		)
	}

	private val wrappedArgs by lazy {
		arguments?.getParcelable<Args>(KEY_WRAPPED_ARGS) ?: throw IllegalStateException("Args can not be null.")
	}
	private val viewModel by lazy {
		val factory = DesignEditorViewModel.Factory(wrappedArgs.selectedDesign)
		ViewModelProvider(this, factory)[DesignEditorViewModel::class.java]
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		viewModel.loadDesigns()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(R.layout.fragment_example_design_editor, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		toolbar = view.findViewById<Toolbar>(R.id.photo_book_design_editor_toolbar).apply {
			navigationIcon = Utils.withTintColorRes(
				requireContext(),
				R.drawable.ic_close_24,
				R.color.secondary
			)
			setNavigationOnClickListener {
				deliverResult(Result(isSubmit = false, selectedDesign = null))
			}
		}
		(activity as AppCompatActivity?)?.setSupportActionBar(toolbar)
		doneBtn = toolbar.findViewById<View>(R.id.photo_book_design_editor_toolbar_done_btn).apply {
			setOnClickListener { viewModel.clickSave() }
		}

//		emptyView = view.findViewById(R.id.photo_book_design_editor_empty_view)
		albumTitleView = view.findViewById<TextView>(R.id.photo_book_editor_album_title).apply {
			if (wrappedArgs.albumTitle.isNullOrBlank()) {
				setText(R.string.album_title)
			} else {
				text = wrappedArgs.albumTitle
			}
		}
		coverPageView = view.findViewById(R.id.editor_cover_page)
		coverFramingLayout = view.findViewById<CustomFramingLayout>(R.id.editor_cover_framing_layout).apply {
			setupRenderer(StubTypes.coverPreviewFrameType.getRenderer())
		}
		coverPreviewImage = view.findViewById(R.id.photo_book_design_editor_cover_preview)

		contentPageView = view.findViewById(R.id.editor_page_content)
		contentFramingLayout = view.findViewById(R.id.editor_content_framing_layout)
		contentImageView = view.findViewById(R.id.editor_content_image_view)

		mainSelectorGroup = view.findViewById(R.id.main_selector_group)
		selectCoverBtn = view.findViewById(R.id.select_cover_btn)
		selectBackgroundBtn = view.findViewById(R.id.select_background_btn)
		selectFrameBtn = view.findViewById(R.id.select_frame_btn)

		bottomPanelGroup = view.findViewById(R.id.bottom_panel_group)
		bottomPanelToolbox = view.findViewById(R.id.bottom_panel_toolbox)
		bottomPanelContainer = view.findViewById(R.id.bottom_panel_container)

		selectCoverBtn.setOnClickListener { viewModel.toCoverSelector() }
		selectBackgroundBtn.setOnClickListener { viewModel.toBackgroundSelector() }
		selectFrameBtn.setOnClickListener { viewModel.toFrameSelector() }

		initBottomPanels()

		viewModel.viewStateLD.observe(viewLifecycleOwner) { renderViewState(it) }
		viewModel.eventsLD.observe(viewLifecycleOwner) { renderEvent(it) }
	}

	private fun initBottomPanels() {
		val itemDecoration = HorizontalSpacingDecoration(
			12.dpToPixelSize(),
			16.dpToPixelSize()
		)

		coverTypesToolboxView = inflateDefaultBottomPanelToolbox()
		coverTypesBottomPanelView = layoutInflater.inflate(
			R.layout.design_editor_bottom_panel_list,
			bottomPanelContainer,
			false
		) as RecyclerView
		coverTypesBottomPanelView.addItemDecoration(itemDecoration)
		coverTypesBottomPanelView.adapter = coverTypesAdapter
		addBottomPanel(coverTypesToolboxView, coverTypesBottomPanelView)

		backgroundTypesToolboxView = inflateDefaultBottomPanelToolbox()
		backgroundTypesBottomPanelView = layoutInflater.inflate(
			R.layout.design_editor_bottom_panel_list,
			bottomPanelContainer,
			false
		) as RecyclerView
		backgroundTypesBottomPanelView.addItemDecoration(itemDecoration)
		backgroundTypesBottomPanelView.adapter = backgroundTypesAdapter
		addBottomPanel(backgroundTypesToolboxView, backgroundTypesBottomPanelView)

		frameToolboxView = inflateDefaultBottomPanelToolbox()
		frameBottomPanelView = layoutInflater.inflate(
			R.layout.design_editor_bottom_panel_list,
			bottomPanelContainer,
			false
		) as RecyclerView
		frameBottomPanelView.addItemDecoration(itemDecoration)
		frameBottomPanelView.adapter = frameTypesAdapter
		addBottomPanel(frameToolboxView, frameBottomPanelView)

		mainSelectorGroup.gone()
		bottomPanelGroup.gone()
	}

	private fun renderEvent(event: PhotoBookDesignEditorEvent) {
		when (event) {
			is PhotoBookDesignEditorEvent.LoadedDesigns -> {
				coverTypesAdapter.setItems(event.coverTypes, event.selectedCover)
				backgroundTypesAdapter.setItems(event.backgroundTypes, event.selectedBackground)
				frameTypesAdapter.setItems(event.frameTypes, event.selectedFrame)

			}
			is PhotoBookDesignEditorEvent.SaveDesign -> deliverResult(event.result)
		}
	}

	private fun renderViewState(viewState: PhotoBookDesignEditorViewState) {
		when (viewState) {
			is PhotoBookDesignEditorViewState.Loading -> {
//				showViewStub(SmartEmptyViewAnimated.Type.EMPTY, SmartEmptyViewAnimated.State.LOADING)
				showViewStub()
			}
			is PhotoBookDesignEditorViewState.Error -> {
//				showViewStub(SmartEmptyViewAnimated.Type.ERROR_UNKNOWN)
				showViewStub()
			}
			is PhotoBookDesignEditorViewState.MainSelector -> {
				if (viewState.showCover) {
					prepareCover(viewState.selectedCover)
					coverPageView.setupAsVisible()
				} else {
					preparePage(viewState.selectedBackground, viewState.selectedFrame)
					coverPageView.setupAsHide()
				}
				contentPageView.visible()
				mainSelectorGroup.visible()
				bottomPanelGroup.gone()
				hideViewStub()
			}
			is PhotoBookDesignEditorViewState.BackgroundSelector -> {
				preparePage(viewState.selectedBackground, viewState.selectedFrame)
				prepareBackgroundBottomPanel()
				hideViewStub()
				coverPageView.hidePage()
				contentPageView.visible()
			}
			is PhotoBookDesignEditorViewState.CoverSelector -> {
				prepareCover(viewState.selectedCover)
				prepareCoverBottomPanel()
				hideViewStub()
				coverPageView.showPage()
				contentPageView.visible()
			}
			is PhotoBookDesignEditorViewState.FrameSelector -> {
				preparePage(viewState.selectedBackground, viewState.selectedFrame)
				prepareFrameBottomPanel()
				hideViewStub()
				coverPageView.hidePage()
				contentPageView.visible()
			}
		}
	}

	private fun prepareCover(selectedCover: CoverTypeItem) {
		coverPageView.setBackgroundResource(selectedCover.coverImageRes)
		coverPreviewImage.setImageResource(selectedCover.previewImageRes)
	}

	private fun preparePage(selectedBackground: BackgroundTypeItem, selectedFrame: FrameTypeItem) {
		contentPageView.setBackgroundResource(selectedBackground.backgroundRes)
		contentFramingLayout.setupRenderer(selectedFrame.type.getRenderer())
		contentFramingLayout.requestLayout()
	}

	private fun showViewStub(
//		type: SmartEmptyViewAnimated.Type,
//		state: SmartEmptyViewAnimated.State = SmartEmptyViewAnimated.State.LOADED
	) {
//		emptyView.apply {
//			this.state = state
//			this.type = type
//			visible()
//		}
		coverPageView.setupAsHide()
		contentPageView.gone()
		mainSelectorGroup.gone()
		bottomPanelGroup.gone()
	}

	private fun hideViewStub() {
//		emptyView.gone()
	}

	private fun hideAllBottomPanels() {
		mainSelectorGroup.hide()
		bottomPanelGroup.gone()
		coverTypesToolboxView.gone()
		coverTypesBottomPanelView.gone()
		backgroundTypesToolboxView.gone()
		backgroundTypesBottomPanelView.gone()
		frameToolboxView.gone()
		frameBottomPanelView.gone()
	}

	private fun prepareCoverBottomPanel() {
		hideAllBottomPanels()
		bottomPanelGroup.visible()
		coverTypesToolboxView.visible()
		coverTypesBottomPanelView.visible()
	}

	private fun prepareBackgroundBottomPanel() {
		hideAllBottomPanels()
		bottomPanelGroup.visible()
		backgroundTypesToolboxView.visible()
		backgroundTypesBottomPanelView.visible()
	}

	private fun prepareFrameBottomPanel() {
		hideAllBottomPanels()
		bottomPanelGroup.visible()
		frameToolboxView.visible()
		frameBottomPanelView.visible()
	}

	private fun addBottomPanel(toolboxView: View, panelView: View) {
		bottomPanelToolbox.addView(toolboxView)
		bottomPanelContainer.addView(panelView)
	}

	private fun inflateDefaultBottomPanelToolbox(): ViewGroup {
		val defaultToolbox = layoutInflater.inflate(
			R.layout.design_editor_bottom_panel_default_toolbox,
			bottomPanelToolbox,
			false
		) as ViewGroup
		defaultToolbox.findViewById<View>(R.id.default_toolbox_back_action).setOnClickListener {
			viewModel.toMainSelector()
		}
		return defaultToolbox
	}

	private fun deliverResult(result: Result) {
		val data = Intent().apply { putExtra(KEY_RESULT, result) }
		//Завершение и передача результата. Необходима реалзиация согласно контракту проекта, куда будет встраиваться
		activity?.onBackPressed()
	}

	@Parcelize
	data class Result(
		val isSubmit: Boolean,
		val selectedDesign: CustomDesign?
	) : Parcelable

	@Parcelize
	private data class Args(
		val albumTitle: String?,
		val selectedDesign: CustomDesign?
	) : Parcelable

	companion object {

		const val KEY_RESULT = "key_custom_design_result"
		private const val KEY_WRAPPED_ARGS = "key_wrapped_args"

		fun createArgs(
			albumTitle: String? = null,
			selectedDesign: CustomDesign? = null
		) = Bundle().apply {
			val wrappedArgs = Args(albumTitle, selectedDesign)
			putParcelable(KEY_WRAPPED_ARGS, wrappedArgs)
		}
	}
}

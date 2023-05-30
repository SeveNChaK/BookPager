package ru.alex.book_pager.sort

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.alex.book_pager.R
import ru.alex.book_pager.Utils
import ru.alex.book_pager.visible

class ExampleSortItemsGridFragment : Fragment() {

	private lateinit var toolbar: Toolbar
	private lateinit var itemsList: RecyclerView

	private val viewModel by lazy {
		val factory = SortItemsGridViewModel.Factory()
		ViewModelProvider(this, factory)[SortItemsGridViewModel::class.java]
	}
	private val adapter by lazy {
		GridItemsAdapter(
			onItemDragged = { holder -> reorderTouchHelper.startDrag(holder) }
		)
	}

	private val reorderTouchCallback by lazy {
		ReorderTouchCallback(
			onMoved = { from, to -> viewModel.onMovingItem(from, to) },
			onDropped = { viewModel.onMovedItem() }
		)
	}

	private val reorderTouchHelper by lazy {
		ItemTouchHelper(reorderTouchCallback)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_example_sort_items_grid, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		toolbar = view.findViewById<Toolbar>(R.id.sort_grid_toolbar).apply {
			navigationIcon = Utils.withTintColorRes(
				requireContext(),
				R.drawable.ic_close_24,
				R.color.secondary
			)
			setNavigationOnClickListener { activity?.onBackPressed() }
		}

		itemsList = view.findViewById(R.id.list)
		val decorator = GridItemSpacingDecoration(resources.getDimensionPixelSize(R.dimen.sort_grid_item_spacing))
		itemsList.addItemDecoration(decorator)
		itemsList.layoutManager = GridLayoutManager(
			requireContext(),
			getGridColumnCount(),
			GridLayoutManager.VERTICAL,
			false
		)
		itemsList.adapter = adapter
		reorderTouchHelper.attachToRecyclerView(itemsList)

		viewModel.sortItemsGridState.observe(viewLifecycleOwner) { renderState(it) }
		viewModel.sortItemsGridEvent.observe(viewLifecycleOwner) { renderEvent(it) }
	}

	private fun renderState(state: SortItemsGridState) {
		when (state) {
			is SortItemsGridState.Data -> {
				hideStubView()
				itemsList.visible()
				adapter.submitList(state.items)
			}
			is SortItemsGridState.Error -> showStubView()
			SortItemsGridState.Loading -> showProgress()
		}
	}

	private fun renderEvent(event: SortItemsGridEvent) {
		when (event) {
			is SortItemsGridEvent.MovingItem -> {
				adapter.notifyItemMoved(event.fromPosition, event.toPosition)
				itemsList.post { itemsList.invalidateItemDecorations() }
			}
		}
	}

	//Реализации заглушек не представлены в данном проекте, так как не имеет значения как они будут сделаны
	private fun showProgress() {
		showStubView() //Показать заглушку с иконкой прогресса
	}

	private fun showStubView() {
		//Показать заглушку с текстом или иконкой в зависимости от ситуации
	}

	private fun hideStubView() {
		//Скрыть заглушку
	}

	private fun getGridColumnCount() = resources.getInteger(R.integer.items_grid_column_count)
}

package ru.alex.book_pager.sort

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import java.util.concurrent.Executors

class SortItemsGridViewModel : ViewModel() {

	private val _sortItemsGridState = MutableLiveData<SortItemsGridState>()
	val sortItemsGridState: LiveData<SortItemsGridState>
		get() = _sortItemsGridState

	private val _sortItemsGridEvent = MutableLiveData<SortItemsGridEvent>()
	val sortItemsGridEvent: LiveData<SortItemsGridEvent>
		get() = _sortItemsGridEvent

	private val itemsDataSourceFactory = ItemsDataSource.Factory()

	private var currentList: PagedList<Item>? = null
	private var tempMovingList: MutableList<Item>? = null
	private val weakCallback = object : PagedList.Callback() {
		override fun onChanged(position: Int, count: Int) {
			//Not implemented
		}

		override fun onInserted(position: Int, count: Int) {
			currentList?.snapshot()?.let { tempMovingList?.addAll(it.subList(position, it.size)) }
		}

		override fun onRemoved(position: Int, count: Int) {
			//Not implemented
		}
	}

	init {
		val pagedConfig = PagedList.Config.Builder()
			.setEnablePlaceholders(false)
			.setPageSize(20)
			.setPrefetchDistance(10)
		LivePagedListBuilder(itemsDataSourceFactory, pagedConfig.build())
			.setFetchExecutor(Executors.newSingleThreadExecutor())
			.build()
			.observeForever {
				currentList = it
				it.apply {
					tempMovingList = snapshot().let { currentItems ->
						mutableListOf<Item>().apply { addAll(currentItems) }
					}
					addWeakCallback(this, weakCallback)
				}
				_sortItemsGridState.postValue(SortItemsGridState.Data(it))
			}
	}

	fun onMovingItem(fromPosition: Int, toPosition: Int) {
		val size = tempMovingList?.size ?: 0
		if (fromPosition in 0 until size && toPosition >= 0 && toPosition < size) {
			tempMovingList?.let {
				val item = it[fromPosition]
				it.removeAt(fromPosition)
				it.add(toPosition, item)
			}
		}
		_sortItemsGridEvent.value = SortItemsGridEvent.MovingItem(fromPosition, toPosition)
	}

	fun onMovedItem() {
		if (tempMovingList.isNullOrEmpty()) {
			return
		}
		StubStorage.reorderedList(tempMovingList!!)
		itemsDataSourceFactory.bindCachedList(tempMovingList)
		currentList?.dataSource?.invalidate()
	}

	class Factory : ViewModelProvider.Factory {
		override fun <T : ViewModel> create(modelClass: Class<T>): T {
			return SortItemsGridViewModel() as T
		}
	}
}

sealed class SortItemsGridState {
	object Loading : SortItemsGridState()
	class Error(val errorText: String) : SortItemsGridState()
	class Data(val items: PagedList<Item>) : SortItemsGridState()
}

sealed class SortItemsGridEvent {
	class MovingItem(val fromPosition: Int, val toPosition: Int) : SortItemsGridEvent()
}

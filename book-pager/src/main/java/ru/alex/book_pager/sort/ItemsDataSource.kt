package ru.alex.book_pager.sort

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource

class ItemsDataSource(
	private val cachedList: List<Item>?
) : PageKeyedDataSource<String, Item>() {

	private companion object {
		const val FIRST_PAGE_KEY = 1
		const val SECOND_PAGE_KEY = 2
	}

	override fun loadInitial(
		params: LoadInitialParams<String>,
		callback: LoadInitialCallback<String, Item>
	) {
		if (cachedList != null) {
			callback.onResult(
				cachedList,
				null,
				null
			)
		} else {
			callback.onResult(
				StubStorage.loadItems(FIRST_PAGE_KEY),
				null,
				SECOND_PAGE_KEY.toString()
			)
		}
	}

	override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, Item>) {
		//Nothing
	}

	override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, Item>) {
		callback.onResult(
			StubStorage.loadItems(params.key.toInt()),
			null
		)
	}

	class Factory : DataSource.Factory<String, Item>() {

		private var cachedList: List<Item>? = null

		override fun create(): DataSource<String, Item> {
			return ItemsDataSource(cachedList)
		}

		fun bindCachedList(cachedList: List<Item>?) {
			this.cachedList = cachedList
		}
	}
}

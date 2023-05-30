package ru.alex.book_pager.sort

object StubStorage {
	private val itemsList = (1..40).map { Item(it) }.toMutableList()
	private val firstPage: List<Item>
		get() = itemsList.subList(0, 21).toList()
	private val secondPage: List<Item>
		get() = itemsList.subList(21, 40).toList()

	fun loadItems(page: Int): List<Item> {
		return when (page) {
			1 -> firstPage
			2 -> secondPage
			else -> listOf()
		}
	}

	fun reorderedList(newList: List<Item>) {
		itemsList.clear()
		itemsList.addAll(newList)
	}
}

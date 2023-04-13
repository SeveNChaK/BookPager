package ru.alex.book_pager.curl_effect.layout

interface FindVisibleItemPositionLayoutManager {
	fun findFirstVisibleItemPosition(): Int
	fun findLastVisibleItemPosition(): Int
}

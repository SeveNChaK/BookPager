package ru.alex.book_pager.curl_effect.example

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.alex.book_pager.R
import ru.alex.book_pager.curl_effect.layout.PageView2D

class Page(
	val background: Drawable,
	val pictures: List<Int>
)

class PagesAdapter(private val pages: List<Page>) : RecyclerView.Adapter<PageViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
		val view = LayoutInflater.from(parent.context)
			.inflate(R.layout.item_curl_page, parent, false)
		return PageViewHolder(view)
	}

	override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
		holder.bind(pages[position])
	}

	override fun getItemCount() = pages.size

}

class PageViewHolder(itemView: View) : ViewHolder(itemView) {

	private val pageView2D = itemView as PageView2D
	private val topLeftImageView = itemView.findViewById<ImageView>(R.id.top_left_image)
	private val topRightImageView = itemView.findViewById<ImageView>(R.id.top_right_image)
	private val bottomLeftImageView = itemView.findViewById<ImageView>(R.id.bottom_left_image)
	private val bottomRightImageView = itemView.findViewById<ImageView>(R.id.bottom_right_image)

	fun bind(page: Page) {
		pageView2D.background = page.background
		topLeftImageView.setImageResource(page.pictures[0])
		topRightImageView.setImageResource(page.pictures[1])
		bottomLeftImageView.setImageResource(page.pictures[2])
		bottomRightImageView.setImageResource(page.pictures[3])
	}
}

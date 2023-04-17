package ru.alex.book_pager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment

class ExampleActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_example)
		navigateTo(ExampleFragment::class.java, addToBackStack = false)
	}

	fun navigateTo(clazz: Class<out Fragment>, addToBackStack: Boolean = true) {
		supportFragmentManager.beginTransaction()
			.replace(R.id.fragment_container, clazz, null)
			.apply { if (addToBackStack) addToBackStack(clazz.simpleName) }
			.commit()
	}
}

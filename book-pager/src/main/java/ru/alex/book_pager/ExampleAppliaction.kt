package ru.alex.book_pager

import android.app.Application

class ExampleApplication : Application() {

	override fun onCreate() {
		super.onCreate()
		ApplicationContextProvider.applicationContext = this
	}
}

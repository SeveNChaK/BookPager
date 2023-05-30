package ru.alex.book_pager

import androidx.lifecycle.MutableLiveData
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

class SingleLiveData<T> : MutableLiveData<T>() {

	private var alreadyNotified = false

	@MainThread
	override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
		super.observe(owner) { t ->
			if (!alreadyNotified) {
				alreadyNotified = true
				observer.onChanged(t)
			}
		}
	}

	@MainThread
	override fun setValue(t: T) {
		alreadyNotified = false
		super.setValue(t)
	}
}

package com.example.ffmpegtest

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ffmpegtest.utils.Event

open class BaseViewModel : ViewModel() {
	val loading = MutableLiveData<Event<Boolean>>(Event(false))
	val error = MutableLiveData<Event<String?>>(Event(null))
	val toast = MutableLiveData<Event<String?>>(Event(null))
	val alert = MutableLiveData<Event<Pair<String, String>?>>(Event(null))
	val noInternetAlert = MutableLiveData<Event<Boolean?>>(Event(null))
}
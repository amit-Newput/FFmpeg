package com.example.ffmpegtest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment

import androidx.viewbinding.ViewBinding

abstract class BaseFragment<T : ViewBinding> : Fragment() {

	private var _binding: T? = null
	protected val binding get() = _binding!!
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		_binding = this.setBinding(inflater, container)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setupViews()
		setupObservers()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	protected open fun enableBack() = true

	protected open fun allowBack() = true



	abstract fun setBinding(inflater: LayoutInflater, container: ViewGroup?): T

	abstract fun setupViews()

	abstract fun setupObservers()
}

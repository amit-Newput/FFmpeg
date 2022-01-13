package com.example.ffmpegtest.ui.main

import android.content.Intent
import android.media.MediaPlayer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.ffmpegtest.BaseFragment
import com.example.ffmpegtest.R
import com.example.ffmpegtest.databinding.MainFragmentBinding
import java.io.IOException

class MainFragment : BaseFragment<MainFragmentBinding>() {

	companion object {
		fun newInstance() = MainFragment()
	}

	private val viewModel: MainViewModel by viewModels()
	private val player: MediaPlayer by lazy { MediaPlayer() }

	override fun setBinding(inflater: LayoutInflater, container: ViewGroup?) = MainFragmentBinding.inflate(inflater, container, false)

	override fun setupViews() {
		with(binding) {
			btInput1.setOnClickListener {
				openGallery(viewModel.REQUEST_CODE_1)
			}

			btInput2.setOnClickListener {
				openGallery(viewModel.REQUEST_CODE_2)
			}

			btInput3.setOnClickListener {
				openGallery(viewModel.REQUEST_CODE_3)
			}

			btApplyEffect.setOnClickListener {
				with(viewModel) {
					input1.value?.let {
						applyEffect(it, echoEffect)
					}
				}
			}

			btMergeFiles.setOnClickListener {
				viewModel.overlayFiles()
				//viewModel.mergeFiles()
			}

			btPlayPause.setOnClickListener {

				if (player.isPlaying)
					stopPlayer()
				else startPlayer()
			}

		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		data?.data?.let { uri ->
			viewModel.onActivityResult(requireContext(), requestCode, uri)
		}

	}

	override fun setupObservers() {
		with(viewModel) {
			input1.observe(viewLifecycleOwner) {
				it?.let {
					binding.tvInput1.text = it.toString()
				}
			}

			input2.observe(viewLifecycleOwner) {
				it?.let {
					binding.tvInput2.text = it.toString()
				}
			}

			input3.observe(viewLifecycleOwner) {
				it?.let {
					binding.tvInput3.text = it.toString()
				}
			}

			error.observe(viewLifecycleOwner) {
				it?.getContentIfNotConsumed()?.let {
					Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
				}
			}

			loading.observe(viewLifecycleOwner) {
				it?.getContentIfNotConsumed()?.let {
					binding.progressBar.isVisible = it
				}
			}
			effectApplied.observe(viewLifecycleOwner) {
				it?.getContentIfNotConsumed()?.let {
					if (it)
						Toast.makeText(requireActivity(), "Effect applied successfully", Toast.LENGTH_SHORT).show()
				}
			}
			effectTime.observe(viewLifecycleOwner) {
				it?.let {
					binding.tvTime.text = it.toString()
				}

			}
		}
	}

	private fun startPlayer() {
		try {
			player.apply {
				reset()
				setDataSource(viewModel.outFile)
				prepare()
			}.run {
				start()
			}
		} catch (e: IOException) {
		}
	}

	private fun stopPlayer() {
		player.pause()
	}

	private fun openGallery(requestCode: Int) {
		val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
		intent.type = "audio/*"
		intent.addCategory(Intent.CATEGORY_OPENABLE)
		intent.addFlags(
				Intent.FLAG_GRANT_READ_URI_PERMISSION
						or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
						or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
						or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
		startActivityForResult(intent, requestCode)
	}
}
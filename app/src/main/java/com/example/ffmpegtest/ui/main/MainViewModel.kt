package com.example.ffmpegtest.ui.main

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegKitConfig
import com.arthenica.ffmpegkit.ReturnCode
import com.example.ffmpegtest.AudioFile
import com.example.ffmpegtest.BaseViewModel
import com.example.ffmpegtest.utils.Event
import java.io.File
import java.lang.StringBuilder

class MainViewModel() : BaseViewModel() {
	val REQUEST_CODE_1 = 111
	val REQUEST_CODE_2 = 222
	val REQUEST_CODE_3 = 333

	private var _input1: MutableLiveData<String> = MutableLiveData()
	val input1: LiveData<String> = _input1
	private var _input2: MutableLiveData<String> = MutableLiveData()
	val input2: LiveData<String> = _input2
	private var _input3: MutableLiveData<String> = MutableLiveData()
	val input3: LiveData<String> = _input3

	val effectTime = MutableLiveData<Long?>(null)

	val effectApplied = MutableLiveData<Event<Boolean?>>(Event(null))

	val outFile = Environment.getExternalStorageDirectory().absolutePath + "/outfile.aac"

	val echoEffect = "-af aecho=0.8:0.9:1000|500:0.7|0.5"
	val speedEffect = "-filter:a atempo=2.0"
	val reverseEffect = "-af areverse"
	val aacConversion = " -c:a aac -b:a 192k "

	fun onActivityResult(context: Context, requestCode: Int, uri: Uri) {
		val path = FFmpegKitConfig.getSafParameterForRead(context, uri)
		when (requestCode) {
			REQUEST_CODE_1 -> _input1.postValue(path)
			REQUEST_CODE_2 -> _input2.postValue(path)
			REQUEST_CODE_3 -> _input3.postValue(path)
			else -> Unit
		}
	}

	fun applyEffect(input: String, effect: String) {
		val command = createCommand(input, effect)
		execute(command)
	}

	private fun createCommand(input: String, effect: String): String {
		val file = File(outFile)
		if (file.exists()) {
			file.delete()
		}
		return "-y -i $input  $effect ${file.absolutePath}"
	}

	private fun execute(command: String) {
		loading.postValue(Event(true))
		val startTime = System.currentTimeMillis()
		FFmpegKit.executeAsync(command) { session ->
			val code = session.returnCode
			loading.postValue(Event(false))
			effectTime.postValue(System.currentTimeMillis() - startTime)
			when {
				ReturnCode.isSuccess(code) -> {
					effectApplied.postValue(Event(true))
				}
				else -> {
					toast.postValue(Event("Something went wrong, executing FFMpeg command"))
				}
			}
		}
	}

	private fun getFileNamesMerged(audioFiles:List<AudioFile>): String {
		return audioFiles.fold("") { acc, file -> "$acc -i ${file.filePath}" }
	}

	private fun getDelayParamsMerged(audioFiles:List<AudioFile>): String {
		return audioFiles.foldIndexed(""){ index,acc, file ->  "$acc[${index+1}]adelay=${file.startOffset}|${file.startOffset}[s${index+1}];"}

	}
	private fun getMixParamsMerged(audioFiles:List<AudioFile>): String {
		val s = audioFiles.foldIndexed("[0]") { index, acc, _ ->  "$acc[s${index+1}]" }
		return "$s amix=${audioFiles.count() +1}[mixout]"
	}

	fun mergeFiles(){
		val file = File(outFile)
		if (file.exists()) {
			file.delete()
		}
		val inputs = listOf(input1.value,input2.value,input3.value).mapNotNull { it?.let {
			AudioFile(it,0)
		} }
		val command = "${getFileNamesMerged(inputs)} -filter_complex ${getDelayParamsMerged(inputs)} ${getMixParamsMerged(inputs)} -map [mixout] -c:a aac -b:a 192k ${file.absolutePath}"
		execute(command)
	}

	fun overlayFiles(){
		val file = File(outFile)
		if (file.exists()) {
			file.delete()
		}
		val inputs = listOf(input1.value,input2.value,input3.value).mapNotNull { it?.let {
			AudioFile(it,0)
		} }
		val command = "-y ${getFileNamesMerged(inputs)} -filter_complex amerge=inputs=${inputs.count()} ${file.absolutePath}"
		execute(command)
	}
}
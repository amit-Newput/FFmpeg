package com.example.ffmpegtest.utils

import androidx.annotation.MainThread

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 * Idea here is that, all the observers who want to handle this event can consume it using their unique scope.
 * All others can observe it and see its value using peekContent.
 */
class Event<out T>(private val content: T) {

	private val consumedScopes = HashSet<String>()

	fun isConsumed(scope: String = "") = consumedScopes.contains(scope)

	/**
	 * Returns the content and prevents its use again.
	 */
	@MainThread
	fun getContentIfNotConsumed(scope: String = ""): T? {
		return if (isConsumed(scope)) {
			null
		} else {
			consumedScopes.add(scope)
			content
		}
	}

	/**
	 * Returns the content, even if it's already been handled.
	 */
	fun peekContent(): T = content
}
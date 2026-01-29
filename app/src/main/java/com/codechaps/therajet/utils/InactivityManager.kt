package com.codechaps.therajet.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class InactivityManager(
    private val timeoutMillis: Long = 60_000L,
    private val onTimeout: () -> Unit
) {
    private var job: Job? = null

    fun start(scope: CoroutineScope) {
        stop()
        job = scope.launch {
            delay(timeoutMillis)
            onTimeout()
        }
    }

    fun reset(scope: CoroutineScope) {
        start(scope)
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}

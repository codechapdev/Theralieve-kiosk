package com.codechaps.therajet.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ClickDebouncer(
    private val scope: CoroutineScope,
    private val delayMs: Long = 300L
) {
    private var job: Job? = null
    
    fun onClick(block: suspend () -> Unit) {
        job?.cancel()
        job = scope.launch {
            delay(delayMs)
            block()
        }
    }
}








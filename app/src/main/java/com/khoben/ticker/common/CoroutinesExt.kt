package com.khoben.ticker.common

import kotlinx.coroutines.*

fun <T> CoroutineScope.onDefaultAsync(call: suspend () -> T): Deferred<T> =
    async(Dispatchers.Default) { call() }

fun <T> CoroutineScope.onIOAsync(call: suspend () -> T): Deferred<T> =
    async(Dispatchers.IO) { call() }

fun <T> CoroutineScope.onIOLaunch(call: suspend () -> T) =
    launch(Dispatchers.IO) { call() }
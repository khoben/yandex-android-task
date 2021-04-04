package com.khoben.ticker.model

sealed class DataState<out H> {
    data class Loading(val status: Boolean): DataState<Nothing>()
    data class Error(val throwable: Throwable): DataState<Nothing>()
    data class Success<out T>(val data: T?): DataState<T>()
}
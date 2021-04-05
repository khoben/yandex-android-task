package com.khoben.ticker.model


sealed class DataBaseLoadingState {
    object Loading : DataBaseLoadingState()
    object Loaded : DataBaseLoadingState()
    class Error(val throwable: Throwable): DataBaseLoadingState()
}

package com.khoben.ticker.common

object ApiErrorProvider : SingleLiveData<Throwable?>() {
    fun init() = this
}
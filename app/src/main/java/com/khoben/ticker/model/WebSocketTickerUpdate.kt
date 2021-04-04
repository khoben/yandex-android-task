package com.khoben.ticker.model

data class WebSocketTickerUpdate(
    val ticker: String? = null,
    val price: Double? = null,
    var throwable: Throwable? = null
)
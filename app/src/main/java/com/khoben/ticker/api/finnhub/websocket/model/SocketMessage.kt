package com.khoben.ticker.api.finnhub.websocket.model

data class SocketMessage(
    val text: String? = null,
    val exception: Throwable? = null
)
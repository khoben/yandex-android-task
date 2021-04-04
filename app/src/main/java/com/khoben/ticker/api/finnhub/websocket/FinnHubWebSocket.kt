package com.khoben.ticker.api.finnhub.websocket

import com.khoben.ticker.api.finnhub.websocket.model.FinnHubSubscribeMessage

class FinnHubWebSocket(private val socket: FinnHubWSService) {
    fun subscribe(subscribe: FinnHubSubscribeMessage) = socket.subscribe(subscribe)
    fun observeEvents() = socket.observeEvents()
    fun observeApi() = socket.observeApi()
}